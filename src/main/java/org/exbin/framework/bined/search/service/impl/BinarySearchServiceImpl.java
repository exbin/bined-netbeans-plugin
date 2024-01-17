/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.bined.search.service.impl;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.highlight.swing.extended.ExtendedHighlightNonAsciiCodeAreaPainter;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.bined.search.ReplaceParameters;
import org.exbin.framework.bined.search.SearchCondition;
import org.exbin.framework.bined.search.SearchParameters;
import org.exbin.framework.bined.search.service.BinarySearchService;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.bined.CharsetStreamTranslator;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.highlight.swing.extended.ExtendedHighlightCodeAreaPainter;

/**
 * Binary search service.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinarySearchServiceImpl implements BinarySearchService {

    private static final int MAX_MATCHES_COUNT = 100;
    private final ExtCodeArea codeArea;
    private final SearchParameters lastSearchParameters = new SearchParameters();

    public BinarySearchServiceImpl(ExtCodeArea codeArea) {
        this.codeArea = codeArea;
    }

    @Override
    public void performFind(SearchParameters searchParameters, SearchStatusListener searchStatusListener) {
        ExtendedHighlightNonAsciiCodeAreaPainter painter = (ExtendedHighlightNonAsciiCodeAreaPainter) codeArea.getPainter();
        SearchCondition condition = searchParameters.getCondition();
        searchStatusListener.clearStatus();
        if (condition.isEmpty()) {
            painter.clearMatches();
            codeArea.repaint();
            return;
        }

        long position;
        switch (searchParameters.getSearchDirection()) {
            case FORWARD: {
                if (searchParameters.isSearchFromCursor()) {
                    position = codeArea.getCaretPosition().getDataPosition();
                } else {
                    position = 0;
                }
                break;
            }
            case BACKWARD: {
                if (searchParameters.isSearchFromCursor()) {
                    position = codeArea.getCaretPosition().getDataPosition() - 1;
                } else {
                    long searchDataSize;
                    switch (condition.getSearchMode()) {
                        case TEXT: {
                            searchDataSize = condition.getSearchText().length();
                            break;
                        }
                        case BINARY: {
                            searchDataSize = condition.getBinaryData().getDataSize();
                            break;
                        }
                        default:
                            throw CodeAreaUtils.getInvalidTypeException(condition.getSearchMode());
                    }
                    position = codeArea.getDataSize() - searchDataSize;
                }
                break;
            }
            default:
                throw CodeAreaUtils.getInvalidTypeException(searchParameters.getSearchDirection());
        }
        searchParameters.setStartPosition(position);

        switch (condition.getSearchMode()) {
            case TEXT: {
                searchForText(searchParameters, searchStatusListener);
                break;
            }
            case BINARY: {
                searchForBinaryData(searchParameters, searchStatusListener);
                break;
            }
            default:
                throw CodeAreaUtils.getInvalidTypeException(condition.getSearchMode());
        }
    }

    /**
     * Performs search by binary data.
     */
    private void searchForBinaryData(SearchParameters searchParameters, SearchStatusListener searchStatusListener) {
        ExtendedHighlightNonAsciiCodeAreaPainter painter = (ExtendedHighlightNonAsciiCodeAreaPainter) codeArea.getPainter();
        SearchCondition condition = searchParameters.getCondition();
        long position = searchParameters.getStartPosition();

        BinaryData searchData = condition.getBinaryData();
        long searchDataSize = searchData.getDataSize();
        BinaryData data = codeArea.getContentData();

        List<ExtendedHighlightNonAsciiCodeAreaPainter.SearchMatch> foundMatches = new ArrayList<>();

        long dataSize = data.getDataSize();
        while (position >= 0 && position <= dataSize - searchDataSize) {
            int matchLength = 0;
            while (matchLength < searchDataSize) {
                if (data.getByte(position + matchLength) != searchData.getByte(matchLength)) {
                    break;
                }
                matchLength++;
            }

            if (matchLength == searchDataSize) {
                ExtendedHighlightNonAsciiCodeAreaPainter.SearchMatch match = new ExtendedHighlightNonAsciiCodeAreaPainter.SearchMatch();
                match.setPosition(position);
                match.setLength(searchDataSize);
                if (searchParameters.getSearchDirection() == SearchParameters.SearchDirection.BACKWARD) {
                    foundMatches.add(0, match);
                } else {
                    foundMatches.add(match);
                }

                if (foundMatches.size() == MAX_MATCHES_COUNT || searchParameters.getMatchMode() == SearchParameters.MatchMode.SINGLE) {
                    break;
                }
            }

            position++;
        }

        painter.setMatches(foundMatches);
        if (!foundMatches.isEmpty()) {
            if (searchParameters.getSearchDirection() == SearchParameters.SearchDirection.BACKWARD) {
                painter.setCurrentMatchIndex(foundMatches.size() - 1);
            } else {
                painter.setCurrentMatchIndex(0);
            }
            ExtendedHighlightNonAsciiCodeAreaPainter.SearchMatch firstMatch = Objects.requireNonNull(painter.getCurrentMatch());
            codeArea.revealPosition(firstMatch.getPosition(), 0, codeArea.getActiveSection());
        }
        lastSearchParameters.setFromParameters(searchParameters);
        searchStatusListener.setStatus(new FoundMatches(foundMatches.size(), foundMatches.isEmpty() ? -1 : painter.getCurrentMatchIndex()), searchParameters.getMatchMode());
        codeArea.repaint();
    }

    /**
     * Performs search by text/characters.
     */
    private void searchForText(SearchParameters searchParameters, SearchStatusListener searchStatusListener) {
        ExtendedHighlightNonAsciiCodeAreaPainter painter = (ExtendedHighlightNonAsciiCodeAreaPainter) codeArea.getPainter();
        SearchCondition condition = searchParameters.getCondition();

        long position = searchParameters.getStartPosition();
        String findText;
        if (searchParameters.isMatchCase()) {
            findText = condition.getSearchText();
        } else {
            findText = condition.getSearchText().toLowerCase();
        }
        long searchDataSize = findText.length();
        BinaryData data = codeArea.getContentData();

        List<ExtendedHighlightNonAsciiCodeAreaPainter.SearchMatch> foundMatches = new ArrayList<>();

        Charset charset = codeArea.getCharset();
        int maxBytesPerChar;
        try {
            CharsetEncoder encoder = charset.newEncoder();
            maxBytesPerChar = (int) encoder.maxBytesPerChar();
        } catch (UnsupportedOperationException ex) {
            maxBytesPerChar = CharsetStreamTranslator.DEFAULT_MAX_BYTES_PER_CHAR;
        }
        byte[] charData = new byte[maxBytesPerChar];
        long dataSize = data.getDataSize();
        while (position >= 0 && position <= dataSize - searchDataSize) {
            int matchCharLength = 0;
            int matchLength = 0;
            while (matchCharLength < searchDataSize) {
                long searchPosition = position + matchLength;
                int bytesToUse = maxBytesPerChar;
                if (searchPosition + bytesToUse > dataSize) {
                    bytesToUse = (int) (dataSize - searchPosition);
                }
                data.copyToArray(searchPosition, charData, 0, bytesToUse);
                char singleChar = new String(charData, charset).charAt(0);
                String singleCharString = String.valueOf(singleChar);
                int characterLength = singleCharString.getBytes(charset).length;

                if (searchParameters.isMatchCase()) {
                    if (singleChar != findText.charAt(matchCharLength)) {
                        break;
                    }
                } else if (singleCharString.toLowerCase().charAt(0) != findText.charAt(matchCharLength)) {
                    break;
                }
                matchCharLength++;
                matchLength += characterLength;
            }

            if (matchCharLength == findText.length()) {
                ExtendedHighlightNonAsciiCodeAreaPainter.SearchMatch match = new ExtendedHighlightNonAsciiCodeAreaPainter.SearchMatch();
                match.setPosition(position);
                match.setLength(matchLength);
                if (searchParameters.getSearchDirection() == SearchParameters.SearchDirection.BACKWARD) {
                    foundMatches.add(0, match);
                } else {
                    foundMatches.add(match);
                }

                if (foundMatches.size() == MAX_MATCHES_COUNT || searchParameters.getMatchMode() == SearchParameters.MatchMode.SINGLE) {
                    break;
                }
            }

            switch (searchParameters.getSearchDirection()) {
                case FORWARD: {
                    position++;
                    break;
                }
                case BACKWARD: {
                    position--;
                    break;
                }
                default:
                    throw CodeAreaUtils.getInvalidTypeException(searchParameters.getSearchDirection());
            }
        }

        painter.setMatches(foundMatches);
        if (!foundMatches.isEmpty()) {
            if (searchParameters.getSearchDirection() == SearchParameters.SearchDirection.BACKWARD) {
                painter.setCurrentMatchIndex(foundMatches.size() - 1);
            } else {
                painter.setCurrentMatchIndex(0);
            }
            ExtendedHighlightNonAsciiCodeAreaPainter.SearchMatch firstMatch = painter.getCurrentMatch();
            codeArea.revealPosition(firstMatch.getPosition(), 0, codeArea.getActiveSection());
        }
        lastSearchParameters.setFromParameters(searchParameters);
        searchStatusListener.setStatus(new FoundMatches(foundMatches.size(), foundMatches.isEmpty() ? -1 : painter.getCurrentMatchIndex()), searchParameters.getMatchMode());
        codeArea.repaint();
    }

    @Override
    public void setMatchPosition(int matchPosition) {
        ExtendedHighlightNonAsciiCodeAreaPainter painter = (ExtendedHighlightNonAsciiCodeAreaPainter) codeArea.getPainter();
        painter.setCurrentMatchIndex(matchPosition);
        ExtendedHighlightNonAsciiCodeAreaPainter.SearchMatch currentMatch = painter.getCurrentMatch();
        codeArea.revealPosition(currentMatch.getPosition(), 0, codeArea.getActiveSection());
        codeArea.repaint();
    }

    @Override
    public void performFindAgain(SearchStatusListener searchStatusListener) {
        ExtendedHighlightNonAsciiCodeAreaPainter painter = (ExtendedHighlightNonAsciiCodeAreaPainter) codeArea.getPainter();
        List<ExtendedHighlightCodeAreaPainter.SearchMatch> foundMatches = painter.getMatches();
        int matchesCount = foundMatches.size();
        if (matchesCount > 0) {
            switch (lastSearchParameters.getMatchMode()) {
                case MULTIPLE:
                    if (matchesCount > 1) {
                        int currentMatchIndex = painter.getCurrentMatchIndex();
                        setMatchPosition(currentMatchIndex < matchesCount - 1 ? currentMatchIndex + 1 : 0);
                        searchStatusListener.setStatus(new FoundMatches(foundMatches.size(), painter.getCurrentMatchIndex()), lastSearchParameters.getMatchMode());
                    }

                    break;
                case SINGLE:
                    switch (lastSearchParameters.getSearchDirection()) {
                        case FORWARD:
                            lastSearchParameters.setStartPosition(foundMatches.get(0).getPosition() + 1);
                            break;
                        case BACKWARD:
                            ExtendedHighlightCodeAreaPainter.SearchMatch match = foundMatches.get(0);
                            lastSearchParameters.setStartPosition(match.getPosition() - 1);
                            break;
                    }

                    SearchCondition condition = lastSearchParameters.getCondition();
                    switch (condition.getSearchMode()) {
                        case TEXT: {
                            searchForText(lastSearchParameters, searchStatusListener);
                            break;
                        }
                        case BINARY: {
                            searchForBinaryData(lastSearchParameters, searchStatusListener);
                            break;
                        }
                        default:
                            throw CodeAreaUtils.getInvalidTypeException(condition.getSearchMode());
                    }
                    break;
            }
        }
    }

    @Override
    public void performReplace(SearchParameters searchParameters, ReplaceParameters replaceParameters) {
        SearchCondition replaceCondition = replaceParameters.getCondition();
        ExtendedHighlightNonAsciiCodeAreaPainter painter = (ExtendedHighlightNonAsciiCodeAreaPainter) codeArea.getPainter();
        ExtendedHighlightNonAsciiCodeAreaPainter.SearchMatch currentMatch = painter.getCurrentMatch();
        if (currentMatch != null) {
            EditableBinaryData editableData = ((EditableBinaryData) codeArea.getContentData());
            editableData.remove(currentMatch.getPosition(), currentMatch.getLength());
            if (replaceCondition.getSearchMode() == SearchCondition.SearchMode.BINARY) {
                editableData.insert(currentMatch.getPosition(), replaceCondition.getBinaryData());
            } else {
                editableData.insert(currentMatch.getPosition(), replaceCondition.getSearchText().getBytes(codeArea.getCharset()));
            }
            painter.getMatches().remove(currentMatch);
            codeArea.repaint();
        }
    }

    @Nonnull
    @Override
    public SearchParameters getLastSearchParameters() {
        return lastSearchParameters;
    }

    @Override
    public void clearMatches() {
        ExtendedHighlightNonAsciiCodeAreaPainter painter = (ExtendedHighlightNonAsciiCodeAreaPainter) codeArea.getPainter();
        painter.clearMatches();
    }
}
