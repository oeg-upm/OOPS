/*
 * SPDX-FileCopyrightText: 2014 María Poveda Villalón <mpovedavillalon@gmail.com>
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Pointer;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;

/**
 * <p>
 * Description: The <tt>SynsetHelp</tt> class represents a word's help using WordNet so as to find out things about a
 * <tt>String</tt> or a pair of <tt>String</tt>.
 * </p>
 * <p>
 * Contains methods for performing basic <tt>String</tt> operations such as:
 * <ol>
 * Find out whether two <tt>String</tt> are synonymous.
 * </ol>
 * <ol>
 * Split a <tt>String</tt> which content is made up of some words into separate <tt>String</tt>.
 * </ol>
 * <ol>
 * Know whether a <tt>String</tt> is an acronym.
 * </ol>
 * <ol>
 * Discover whether two <tt>String</tt> are meronyms.
 * </ol>
 * <ol>
 * Recognize whether two <tt>String</tt> are hypernyms.
 * </ol>
 * </p>
 *
 * @author Ana Maria Ruiz Jimenez
 *
 * @version 1.0
 */

public class SynsetHelp {

    private final Dictionary dictionary;
    private final StopWords stop;

    /**
     * Initializes a newly created <tt>SynsetHelp</tt> object so that it represents an empty word's help.
     */
    public SynsetHelp() throws IOException, JWNLException {
        dictionary = Dictionary.getDefaultResourceInstance();
        stop = new StopWords();
    }

    /**
     * Finds out if this <tt>stringA1</tt> is synonymous to another <tt>stringA2</tt>.
     *
     * @param stringA1
     *     the <tt>String</tt> to be compared with.
     * @param stringA2
     *     the <tt>String</tt> to be compared.
     *
     * @return <tt>true</tt> if and only if these string are synonymous, <tt>false</tt> in other fact.
     *
     * @throws IOException
     */
    public boolean areSynonymousNouns(final String stringA1, final String stringA2) throws JWNLException {

        final String stringA1Lower = stringA1.toLowerCase();
        final String stringA2Lower = stringA2.toLowerCase();
        final IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, stringA1);
        if (indexWord != null) {
            final List<Synset> synsets = indexWord.getSenses();
            for (final Synset nounSynset : synsets) {
                // Array of synonymous words and phrases
                final List<Word> words = nounSynset.getWords();
                for (final Word word : words) {
                    // System.out.println(wordsForms[j]);
                    final String wordLower = word.getLemma().toLowerCase();
                    // If the word is not stringA1 which you have chosen to compare, then you find out if
                    // this word is a synonymous of stringA2
                    if (!wordLower.equals(stringA1Lower) && wordLower.equals(stringA2Lower)) {
                        // System.out.println(stringA1 + " AND " + stringA2 + " ARE SYNONYMOUS: " );
                        return true;
                    }
                }
                // System.out.println("_____________");
            }
        }

        return false;
    }

    /**
     * Splits a <tt>String</tt> into different <tt>String</tt> whether the first one's content is made up of some words.
     * Takes into account acronyms.
     *
     * @param complexWord
     *     the <tt>String</tt> to be split.
     *
     * @return a list containing the specific <tt>String</tt> which are made up the <tt>complexWord</tt>.
     * <p>
     * The string <tt>"Idiot_box-DVD.Television.A.I.D.S"</tt>, for example, yields a list with the following
     * result:
     * <ol>
     * Idiot
     * </ol>
     * <ol>
     * box
     * </ol>
     * <ol>
     * DVD
     * </ol>
     * <ol>
     * Television
     * </ol>
     * <ol>
     * A
     * </ol>
     * <ol>
     * I
     * </ol>
     * <ol>
     * D
     * </ol>
     * <ol>
     * S
     * </ol>
     * </p>
     */
    public List<String> splitWordsAcronym(final String complexWord) {

        final List<String> separateAcronym = new ArrayList<>();
        String newWord = "";
        boolean exit;

        final int length = complexWord.length();
        int i = 0;
        while (i < length) {
            final Character firstChar = complexWord.charAt(i);
            // If the first char is Upper case, go on searching whether the following chars are Upper case too.
            if (Character.isUpperCase(firstChar)) {
                i++;
                newWord = firstChar.toString();
                exit = false;
                while (i < length && !exit) {
                    Character nextChar = complexWord.charAt(i);
                    i++;
                    if (Character.isUpperCase(nextChar)) {
                        newWord = newWord.concat(nextChar.toString());
                        // If I can explore the complex string
                        if (i < length) {
                            final Character lastOne = complexWord.charAt(i);
                            // The next char is lower case, undo the last concat because it belongs to next word
                            if (Character.isLowerCase(lastOne)) {
                                final String newStr = newWord.substring(0, newWord.length() - 1);
                                newWord = newStr;
                                separateAcronym.add(newWord);
                                newWord = nextChar.toString();
                                nextChar = lastOne;
                            }
                        }
                        // If the acronym is situated at the end of the complexWord
                        if (i == length && Character.isUpperCase(nextChar)) {
                            separateAcronym.add(newWord);
                        }
                    } else {
                        // If the second char of a word is lower case
                        if (Character.isLowerCase(nextChar)) {
                            newWord = newWord.concat(nextChar.toString());
                            if (i < length) {
                                Character anotherNextChar = complexWord.charAt(i);
                                while (i < length && Character.isLowerCase(anotherNextChar)) {
                                    newWord = newWord.concat(anotherNextChar.toString());
                                    i++;
                                    if (i < length) {
                                        anotherNextChar = complexWord.charAt(i);
                                    }
                                }
                                // We have a complete word, then we have to store it as a word
                                separateAcronym.add(newWord);
                                exit = true;
                            } else {
                                // If this char is the last one char of the String complexWord
                                separateAcronym.add(newWord);
                            }
                        } else {
                            // If nextChar is not a letter jump it
                            if (!Character.isUpperCase(nextChar) && !Character.isLowerCase(nextChar) && (i < length)) {
                                exit = true;
                                separateAcronym.add(newWord);
                                nextChar = complexWord.charAt(i);
                                newWord = firstChar.toString();
                            }
                        }
                    } // else

                } // 2while
                  // If newWord is the last one with which size is one and Upper
                if (i == complexWord.length() && newWord.length() == 1)
                    separateAcronym.add(newWord);
            } else {
                // If the first char is Lower case, go on searching whether the following chars are Lower case too.
                if (Character.isLowerCase(firstChar)) {
                    newWord = getOneLowerWord(complexWord, i);
                    separateAcronym.add(newWord);
                    i = i + newWord.length();
                } else {
                    // If the char is another symbol then don't considerate it
                    if (!Character.isLowerCase(firstChar) && !Character.isUpperCase(firstChar)) {
                        i++;
                    }
                }
            }
        } // 1while
        return separateAcronym;
    }

    /**
     * Gets and returns a new <tt>String</tt> that is a sub-string of this string. The string begins at the specified
     * <tt>position</tt> and extends until to the next character is Upper letter or until finding a no character or at
     * the end of this string.
     *
     * @param complexWord
     *     the string where search for.
     * @param position
     *     the beginning index, inclusive.
     *
     * @return the specified sub-string.
     *
     * @pre The <tt>char</tt> value at the specified index of this string has to be a lower letter.
     */
    private String getOneLowerWord(final String complexWord, final int position) {
        final int length = complexWord.length();

        boolean endWord = false;
        Character chr = complexWord.charAt(position);
        String lower = chr.toString();
        int i = position + 1;
        while (i < length && !endWord) {
            chr = complexWord.charAt(i);
            if (Character.isLowerCase(chr)) {
                lower = lower.concat(chr.toString());
                i++;
            } else {
                endWord = true;
            }
        }
        return lower;
    }

    // Almost cases the hashmap only will have one element or will be empty
    /**
     * Recognizes whether between these string there are some sub-string which are words and at the same time any of
     * these words belong of each <tt>String</tt> are synonymous.
     *
     * @param complex1
     *     the <tt>String</tt> to be compared with.
     * @param complex2
     *     the <tt>String</tt> to be compared.
     *
     * @return A <tt>HashMap</tt> whose mappings are:
     * <li>
     * <ol>
     * the first part of the hashmap is part of the <tt>complex1</tt> which es synonym of this entry's second
     * part.
     * </ol>
     * <li>
     * <ol>
     * the second part of the hashmap is part of the <tt>complex2</tt> which es synonym of this entry's first
     * part.
     * </ol>
     * <p>
     * The <tt>HashMap</tt> can be empty if no exists synonymous between <tt>complex1</tt> and
     * <tt>complex2</tt>.
     * </p>
     *
     * @throws IOException
     */
    public Map<String, String> containSynonymWord(final String complex1, final String complex2) throws JWNLException {
        final Map<String, String> hm = new HashMap<>();
        List<String> words1 = this.splitWordsAcronym(complex1);
        words1 = this.buildCompoundWords(words1);
        List<String> words2 = this.splitWordsAcronym(complex2);
        words2 = this.buildCompoundWords(words2);
        Iterator<String> itWords1 = words1.iterator();

        while (itWords1.hasNext()) {
            final String word1 = itWords1.next();
            final Iterator<String> itWords2 = words2.iterator();
            while (itWords2.hasNext()) {
                final String word2 = itWords2.next();
                if (this.areSynonymousNouns(word1, word2)) {
                    hm.put(word1, word2);
                }
            }
        }
        return hm;
    }

    /**
     * if all the non stop words in complex1 have a synonym in complex2 a lo mejor hay que añadir el caso de que sean la
     * misma palabra, no solo sinónimos.
     */
    public boolean containSynonymsForAllNoStopWords(final String complex1, final String complex2) throws JWNLException {
        final Map<String, String> hm = new HashMap<String, String>();
        List<String> words1 = this.splitWordsAcronym(complex1);
        words1 = this.buildCompoundWords(words1);
        List<String> words2 = this.splitWordsAcronym(complex2);
        words2 = this.buildCompoundWords(words2);

        // cuanto las no stop words
        int noStopWords = 0;
        final List<String> listWords1 = this.splitWordsAcronym(complex1);
        final Iterator<String> itListWords1 = listWords1.iterator();
        while (itListWords1.hasNext()) {
            final String word = itListWords1.next();
            if (!stop.isStopWord(word)) {
                noStopWords++;
            }
        }

        final Iterator<String> itWords1 = words1.iterator();
        while (itWords1.hasNext()) {
            final String word1 = itWords1.next();
            final Iterator<String> itWords2 = words2.iterator();
            while (itWords2.hasNext()) {
                final String word2 = itWords2.next();
                if (this.areSynonymousNouns(word1, word2) || word1.toLowerCase().contentEquals(word2.toLowerCase())) {
                    hm.put(word1, word2);
                }
            }
        }
        if (noStopWords <= hm.size()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Finds out if a <tt>String</tt> is an acronym.
     *
     * @param word
     *     the <tt>String</tt> to search for.
     *
     * @return <tt>true</tt> if and only if <tt>word</tt> is an acronym, <tt>false</tt> in other fact.
     */
    public boolean isAcronym(final String word) {
        boolean isAcronym = true;
        int i = 0;
        while (i < word.length() && isAcronym) {
            if (Character.isLowerCase(word.charAt(0))) {
                isAcronym = false;
            }
            i++;
        }
        return isAcronym;
    }

    /**
     * Finds out if this <tt>String word1</tt> is hypernym to another <tt>String word2</tt>.
     *
     * @param word1
     *     the <tt>String</tt> to be compared with.
     * @param word2
     *     the <tt>String</tt> to be compared.
     * @param recursiveLevel
     *     represents the hierarchical level to search the hypernym connection.
     *     <p>
     *     For example if we have the words <tt>"business"</tt> and <tt>"organization"</tt> we know
     *     <li>
     *     <ol>
     *     <tt>"business"</tt> is hypernym of <tt>"enterprise"</tt>
     *     </ol>
     *     <li>
     *     <ol>
     *     and this at the same time is hypernym of <tt>"organization"</tt>
     *     </ol>
     *     then <tt>"business"</tt> and <tt>"organization"</tt> are hypernyms but not directly.
     *     </p>
     *     <p>
     *     In this case using the value 2 in <tt>recursiveLevel</tt> the result will be <tt>true</tt> and using
     *     the value 1 will be <tt>false</tt>.
     *     </p>
     *
     * @return <tt>true</tt> if and only if these string are hypernyms keeping in mind hierarchy's level, <tt>false</tt>
     * in other fact.
     *
     * @throws IOException
     */
    public boolean areHypernyms(final String word1, final String word2, final int recursiveLevel) throws JWNLException {

        // System.out.println("Level:"+recursiveLevel);
        if (recursiveLevel > 0) {
            final IndexWord indexWord1 = dictionary.getIndexWord(POS.NOUN, word1);
            if (indexWord1 == null) {
                return false;
            }
            final List<Synset> synsets = indexWord1.getSenses();
            final List<String> searchAgain = new ArrayList<>();

            for (final Synset nounSynset : synsets) {
                final List<Word> words = nounSynset.getWords();
                for (final Word word : words) {
                    final String searchWord = word.getLemma();
                    if (searchWord.equalsIgnoreCase(word1)) {
                        // System.out.println("Word:"+searchWord);
                        // Search the hypernyms
                        // NounSynset[] hypernyms = nounSynset.getHypernyms();
                        final List<Pointer> hypernymPointers = nounSynset.getPointers(PointerType.HYPERNYM);
                        for (final Pointer hypernym : hypernymPointers) {
                            // String[] hypernymsString = hypernym.getWordForms();
                            final List<Word> hypernymsWords = hypernym.getTargetSynset().getWords();
                            for (final Word hypernymsWord : hypernymsWords) {
                                final String hypernymStr = hypernymsWord.getLemma();
                                if (hypernymStr.equalsIgnoreCase(word2)) {
                                    return true;
                                } else {
                                    // Store the hypernym to find out whether it is hypernym of word1
                                    searchAgain.add(hypernymStr);
                                }
                                // System.out.println("\tHypernym:"+hypernymStr);
                            }
                        }
                    } // if
                }
            }
            // If we have not found yet whether two words are hypernyms, we have to continue searching
            int recurLevel = recursiveLevel - 1;
            for (final String candidate : searchAgain) {
                if (areHypernyms(candidate, word2, recurLevel)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasSynsets(final String word) throws JWNLException {

        for (final POS pos : POS.getAllPOS()) {
            final IndexWord current = dictionary.getIndexWord(pos, word);
            if (current != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds out if this <tt>String word1</tt> is meronym to another <tt>String word2</tt>.
     *
     * @param word1
     *     the <tt>String</tt> to be compared with.
     * @param word2
     *     the <tt>String</tt> to be compared.
     * @param typeMeronym
     *     can be one of these values <tt>MEMBER PART SUBSTANCE</tt>.
     *     <p>
     *     <li>
     *     <ol>
     *     <tt>PART</tt> : the kind is part meronyms (inherited parts). For example, part meronyms for
     *     <tt>"airplane"</tt> include <tt>"wing"</tt> and <tt>"fuselage"</tt>.
     *     </ol>
     *     <li>
     *     <ol>
     *     <tt>MEMBER</tt>: the kind is member meronyms which are the parts that make up the whole. For example,
     *     member meronyms of <tt>"Roman Alphabet"</tt> are <tt>"A"</tt>, <tt>"B"</tt>, <tt>"C"</tt>, etc.
     *     </ol>
     *     <li>
     *     <ol>
     *     <tt>SUBSTANCE</tt>: the kind is substances that make up the thing represented by the <tt>String</tt>.
     *     For example, a substance meronym of "chocolate" is "cocoa".
     *     </ol>
     *     </li>
     *     </p>
     * @param recursiveLevel
     *     represents the hierarchical level to search the meronym connection.
     *     <p>
     *     For example if we have the words <tt>"human"</tt> and <tt>"finger"</tt> we know
     *     <li>
     *     <ol>
     *     <tt>"finger"</tt> is meronym of <tt>"hand"</tt>
     *     </ol>
     *     <li>
     *     <ol>
     *     and this at the same time is meronym of <tt>"human"</tt>
     *     </ol>
     *     then <tt>"finger"</tt> and <tt>"human"</tt> are meronyms but not directly.
     *     </p>
     *     <p>
     *     In this case using the value 2 in <tt>recursiveLevel</tt> the result will be <tt>true</tt> and using
     *     the value 1 will be <tt>false</tt>.
     *     </p>
     *
     * @return <tt>true</tt> if and only if these string are meronyms keeping in mind hierarchy's level, <tt>false</tt>
     * in other fact.
     *
     * @throws IOException
     */
    public boolean areMeronyms(final String word1, final String word2, final String typeMeronym,
            final int recursiveLevel) throws JWNLException {

        if (recursiveLevel > 0) {
            final IndexWord indexWord1 = dictionary.getIndexWord(POS.NOUN, word1);
            if (indexWord1 == null) {
                return false;
            }
            final List<Synset> synsets = indexWord1.getSenses();
            final List<String> searchAgain = new ArrayList<>();

            for (final Synset nounSynset : synsets) {
                final List<Word> words = nounSynset.getWords();
                for (final Word word : words) {
                    // final String searchWord = word.getLemma();
                    // String searchWord = set[z];
                    // if (searchWord.equals(word1) && !areMeronyms){
                    // System.out.println("Word:"+searchWord);
                    final List<Pointer> meronymPointers;
                    /** getMemberMeronyms()**getPartMeronyms()**getSubstanceMeronyms() **/
                    // Search the meronyms
                    if (typeMeronym.equals("MEMBER")) {
                        meronymPointers = word.getPointers(PointerType.MEMBER_MERONYM);
                    } else if (typeMeronym.equals("SUBSTANCE")) {
                        meronymPointers = word.getPointers(PointerType.SUBSTANCE_MERONYM);
                    } else {
                        meronymPointers = word.getPointers(PointerType.PART_MERONYM);
                    }
                    for (final Pointer meronymPointer : meronymPointers) {
                        final List<Word> meronymWords = meronymPointer.getTargetSynset().getWords();
                        for (final Word hypernymsWord : meronymWords) {
                            final String meronymStr = hypernymsWord.getLemma();
                            if (meronymStr.equalsIgnoreCase(word2)) {
                                return true;
                            } else {
                                // Store the meronym to find out whether it is meronym of word1
                                searchAgain.add(meronymStr);
                            }
                            // System.out.println("\tMeronym:"+meronymStr);
                        }
                    }
                }
            }
            final int recurLevel = recursiveLevel - 1;
            for (final String candidate : searchAgain) {
                if (areMeronyms(candidate, word2, typeMeronym, recurLevel)) {
                    return true;
                }
            }
        } // if recursive level

        return false;
    }

    /**
     * Recognizes whether between these string there are some sub-string which are words and at the same time any of
     * these words belong of each <tt>String</tt> are meronyms.
     *
     * @param complex1
     *     the <tt>String</tt> to be compared with.
     * @param complex2
     *     the <tt>String</tt> to be compared.
     * @param typeMeronym
     *     can be one of these values <tt>MEMBER PART SUBSTANCE</tt>.
     *     <p>
     *     <li>
     *     <ol>
     *     <tt>PART</tt>: the kind is part meronyms (inherited parts). For example, part meronyms for
     *     <tt>"airplane"</tt> include <tt>"wing"</tt> and <tt>"fuselage"</tt>.
     *     </ol>
     *     <li>
     *     <ol>
     *     <tt>MEMBER</tt>: the kind is member meronyms which are the parts that make up the whole. For example,
     *     member meronyms of <tt>"Roman Alphabet"</tt> are <tt>"A"</tt>, <tt>"B"</tt>, <tt>"C"</tt>, etc.
     *     </ol>
     *     <li>
     *     <ol>
     *     <tt>SUBSTANCE</tt>: the kind is substances that make up the thing represented by the <tt>String</tt>.
     *     For example, a substance meronym of "chocolate" is "cocoa".
     *     </ol>
     *     </li>
     *     </p>
     * @param recursiveLevel
     *     represents the hierarchical level to search the meronym connection.
     *     <p>
     *     For example if we have the words <tt>"human"</tt> and <tt>"finger"</tt> we know
     *     <li>
     *     <ol>
     *     <tt>"finger"</tt> is meronym of <tt>"hand"</tt>
     *     </ol>
     *     <li>
     *     <ol>
     *     and this at the same time is meronym of <tt>"human"</tt>
     *     </ol>
     *     then <tt>"finger"</tt> and <tt>"human"</tt> are meronyms but not directly.
     *     </p>
     *     <p>
     *     In this case using the value 2 in <tt>recursiveLevel</tt> the result will be <tt>true</tt> and using
     *     the value 1 will be <tt>false</tt>
     *     </p>
     *
     * @return A <tt>HashMap</tt> whose mappings are:
     * <li>
     * <ol>
     * the first part of the hashmap is part of the <tt>complex1</tt> which es meronym of this entry's second
     * part.
     * </ol>
     * <li>
     * <ol>
     * the second part of the hashmap is part of the <tt>complex2</tt> which es meronym of this entry's first
     * part.
     * </ol>
     * <p>
     * The <tt>HashMap</tt> can be empty if no exists meronyms between <tt>complex1</tt> and <tt>complex2</tt>.
     * </p>
     *
     * @throws IOException
     */
    public Map<String, String> containMeronymWord(final String complex1, final String complex2,
            final String typeMeronym, final int recursiveLevel) throws JWNLException {
        final Map<String, String> result = new HashMap<String, String>();
        List<String> words1 = this.splitWordsAcronym(complex1);
        words1 = this.buildCompoundWords(words1);
        List<String> words2 = this.splitWordsAcronym(complex2);
        words2 = this.buildCompoundWords(words2);

        for (final String word1 : words1) {
            for (final String word2 : words2) {
                if (this.areMeronyms(word1, word2, typeMeronym, recursiveLevel)) {
                    result.put(word1, word2);
                }
            }
        }

        return result;
    }

    /**
     * Recognizes whether between these string there are some sub-string which are words and at the same time any of
     * these words belong of each <tt>String</tt> are hypernyms.
     *
     * @param complex1
     *     the <tt>String</tt> to be compared with.
     * @param complex2
     *     the <tt>String</tt> to be compared.
     * @param recursiveLevel
     *     represents the hierarchical level to search the hypernym connection.
     *     <p>
     *     For example if we have the words <tt>"business"</tt> and <tt>"organization"</tt> we know
     *     <li>
     *     <ol>
     *     <tt>"business"</tt> is hypernym of <tt>"enterprise"</tt>
     *     </ol>
     *     <li>
     *     <ol>
     *     and this at the same time is hypernym of <tt>"organization"</tt>
     *     </ol>
     *     then <tt>"business"</tt> and <tt>"organization"</tt> are hypernyms but not directly.
     *     </p>
     *     <p>
     *     In this case using the value 2 in <tt>recursiveLevel</tt> the result will be <tt>true</tt> and using
     *     the value 1 will be <tt>false</tt>
     *     </p>
     *
     * @return A <tt>HashMap</tt> whose mappings are:
     * <li>
     * <ol>
     * the first part of the hashmap is part of the <tt>complex1</tt> which es hypernym of this entry's second
     * part.
     * </ol>
     * <li>
     * <ol>
     * the second part of the hashmap is part of the <tt>complex2</tt> which es hypernym of this entry's first
     * part.
     * </ol>
     * <p>
     * The <tt>HashMap</tt> can be empty if no exists hypernyms between <tt>complex1</tt> and <tt>complex2</tt>.
     * </p>
     *
     * @throws IOException
     */
    public Map<String, String> containHypernymWord(final String complex1, final String complex2,
            final int recursiveLevel) throws JWNLException {
        final Map<String, String> hm = new HashMap<>();
        List<String> words1 = this.splitWordsAcronym(complex1);
        words1 = this.buildCompoundWords(words1);
        List<String> words2 = this.splitWordsAcronym(complex2);
        words2 = this.buildCompoundWords(words2);
        final Iterator<String> itWords1 = words1.iterator();

        while (itWords1.hasNext()) {
            final String word1 = itWords1.next();
            final Iterator<String> itWords2 = words2.iterator();
            while (itWords2.hasNext()) {
                final String word2 = itWords2.next();
                if (this.areHypernyms(word1, word2, recursiveLevel)) {
                    hm.put(word1, word2);
                }
            }
        }

        return hm;
    }

    /**
     * Builds the all possible combinations with all simple words but only orderly.
     *
     * @param words
     *     a list containing words.
     *
     * @return a list containing all the list's <tt>word</tt> of input parameter and the all possible combinations with
     * all simple words but only orderly.
     * <p>
     * The next list
     * <ol>
     * National
     * </ol>
     * <ol>
     * Aeronautics
     * </ol>
     * <ol>
     * Space
     * </ol>
     * <ol>
     * administration
     * </ol>
     * for example, yields a list with the following result:
     * <ol>
     * National
     * </ol>
     * <ol>
     * Aeronautics
     * </ol>
     * <ol>
     * Space
     * </ol>
     * <ol>
     * Administration
     * </ol>
     * <ol>
     * National Aeronautics
     * </ol>
     * <ol>
     * National Aeronautics Space
     * </ol>
     * <ol>
     * National Aeronautics Space Administration
     * </ol>
     * <ol>
     * Aeronautics Space
     * </ol>
     * <ol>
     * Aeronautics Space Administration
     * </ol>
     * <ol>
     * Space Administration
     * </ol>
     * </p>
     */
    public List<String> buildCompoundWords(final List<String> words) {
        final List<String> compoundWords = new ArrayList<>();
        final Iterator<String> firstIt = words.iterator();

        // First at all put all the simple words into compoundWords list
        while (firstIt.hasNext()) {
            compoundWords.add(firstIt.next());
        }

        // Then build the all possible combinations with all simple words but only catch them orderly
        for (int i = 0; i < words.size(); i++) {
            for (int j = i + 1; j < words.size(); j++) {
                final String newWord;
                if (j == i + 1) {
                    newWord = words.get(i) + " " + words.get(j);
                } else {
                    newWord = compoundWords.get(compoundWords.size() - 1) + " " + words.get(j);
                }
                compoundWords.add(newWord);

            }
        }
        return compoundWords;
    }

    /**
     * Recognizes if two words are equals ignoring upper and lower case and other signs.
     * <p>
     * These words can have white spaces and other sign.
     * </p>
     *
     * @param word1
     *     the <tt>String</tt> to be compared with.
     * @param word2
     *     the <tt>String</tt> to be compared.
     *
     * @return <tt>true</tt> if and only if these words are equals, <tt>false</tt> in other fact.
     */
    public boolean areEqualsWords(final String word1, final String word2) {

        boolean areEquals = true;

        final List<String> words1 = this.splitWordsAcronym(word1);
        final List<String> words2 = this.splitWordsAcronym(word2);
        if (words1.size() == words2.size()) {
            for (int i = 0; i < words1.size() && areEquals; i++) {
                if (!words1.get(i).equalsIgnoreCase(words2.get(i))) {
                    areEquals = false;
                    break;
                }
            }
        } else {
            areEquals = false;
        }

        return areEquals;
    }
}
