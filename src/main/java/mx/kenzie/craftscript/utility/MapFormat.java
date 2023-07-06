package mx.kenzie.craftscript.utility;

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

import mx.kenzie.craftscript.variable.Wrapper;

import java.io.Serial;
import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Map;


/**
 * A text format similar to <code>MessageFormat</code>
 * but using string rather than numeric keys.
 * You might use use this formatter like this:
 * <pre>MapFormat.format("Hello {name}", map);</pre>
 * Or to have more control over it:
 * <pre>
 * Map m = new HashMap ();
 * m.put ("KEY", "value");
 * MapFormat f = new MapFormat (m);
 * f.setLeftBrace ("__");
 * f.setRightBrace ("__");
 * String result = f.format ("the __KEY__ here");
 * </pre>
 *
 * @author Slavek Psenicka
 * @see MessageFormat
 */
public class MapFormat extends Format {

    private static final int BUFSIZE = 255;

    /**
     * Array with to-be-skipped blocks
     */

    //private RangeList skipped;
    @Serial
    private static final long serialVersionUID = -7695811542873819435L;

    /**
     * Locale region settings used for number and date formatting
     */
    private final Locale locale = Locale.getDefault();

    /**
     * Left delimiter
     */
    private String ldel = "{"; // NOI18N

    /**
     * Right delimiter
     */
    private String rdel = "}"; // NOI18N

    /**
     * Used formatting map
     */
    private Map argmap;

    /**
     * Offsets to {} expressions
     */
    private int[] offsets;

    /**
     * Keys enclosed by {} brackets
     */
    private String[] arguments;

    /**
     * Max used offset
     */
    private int maxOffset;

    /**
     * Should be thrown an exception if key was not found?
     */
    private boolean throwex = false;

    /**
     * Exactly match brackets?
     */
    private boolean exactmatch = true;

    /**
     * Constructor.
     * For common work use  <code>format(pattern, arguments) </code>.
     *
     * @param arguments keys and values to use in the format
     */
    public MapFormat(Map<?, ?> arguments) {
        super();
        setMap(arguments);
    }

    /**
     * Designated method. It gets the string, initializes HashFormat object
     * and returns converted string. It scans  <code>pattern</code>
     * for {} brackets, then parses enclosed string and replaces it
     * with argument's  <code>get()</code> value.
     *
     * @param pattern   String to be parsed.
     * @param arguments Map with key-value pairs to replace.
     * @return Formatted string
     */
    public static String format(String pattern, Map arguments) {
        MapFormat temp = new MapFormat(arguments);

        return temp.format(pattern);
    }

    // unused so removed --jglick

    /**
     * Search for comments and quotation marks.
     * Prepares internal structures.
     * @param pattern String to be parsed.
     * @param lmark Left mark of to-be-skipped block.
     * @param rmark Right mark of to-be-skipped block or null if does not exist (// comment).
    private void process(String pattern, String lmark, String rmark)
    {
    int idx = 0;
    while (true) {
    int ridx = -1, lidx = pattern.indexOf(lmark,idx);
    if (lidx >= 0) {
    if (rmark != null) {
    ridx = pattern.indexOf(rmark,lidx + lmark.length());
    } else ridx = pattern.length();
    } else break;
    if (ridx >= 0) {
    skipped.put(new Range(lidx, ridx-lidx));
    if (rmark != null) idx = ridx+rmark.length();
    else break;
    } else break;
    }
    }
     */
    /**
     * Returns the value for given key. Subclass may define its own beahvior of
     * this method. For example, if key is not defined, subclass can return <not defined>
     * string.
     *
     * @param key Key.
     * @return Value for this key.
     */
    protected Object processKey(String key) {
        return argmap.get(key);
    }

    /**
     * Scans the pattern and prepares internal variables.
     *
     * @param newPattern String to be parsed.
     * @throws IllegalArgumentException if number of arguments exceeds BUFSIZE or
     *                                  parser found unmatched brackets (this exception should be switched off
     *                                  using setExactMatch(false)).
     */
    public String processPattern(String newPattern) throws IllegalArgumentException {
        int idx = 0;
        int offnum = -1;
        StringBuilder outpat = new StringBuilder();
        offsets = new int[BUFSIZE];
        arguments = new String[BUFSIZE];
        maxOffset = -1;
        //skipped = new RangeList();
        // What was this for??
        //process(newPattern, "\"", "\""); // NOI18N
        while (true) {
            int ridx = -1;
            int lidx = newPattern.indexOf(ldel, idx);
            if (lidx >= 0) {
                ridx = newPattern.indexOf(rdel, lidx + ldel.length());
            } else break;
            if (++offnum >= BUFSIZE) {
                throw new IllegalArgumentException("TooManyArguments");
            }
            if (ridx < 0) {
                if (exactmatch) {
                    throw new IllegalArgumentException("UnmatchedBraces");
                } else break;
            }
            outpat.append(newPattern, idx, lidx);
            offsets[offnum] = outpat.length();
            arguments[offnum] = newPattern.substring(lidx + ldel.length(), ridx);
            idx = ridx + rdel.length();
            maxOffset++;
        }
        outpat.append(newPattern.substring(idx));
        return outpat.toString();
    }

    /**
     * Formats object.
     *
     * @param obj Object to be formatted into string
     * @return Formatted object
     */
    private String formatObject(Object obj) {
        return Wrapper.of(obj).toString();
    }

    /**
     * Formats the parsed string by inserting table's values.
     *
     * @param pat    a string pattern
     * @param result Buffer to be used for result.
     * @param fpos   position
     * @return Formatted string
     */
    public StringBuffer format(Object pat, StringBuffer result, FieldPosition fpos) {
        String pattern = processPattern((String) pat);
        int lastOffset = 0;

        for (int i = 0; i <= maxOffset; ++i) {
            int offidx = offsets[i];
            result.append(pattern, lastOffset, offsets[i]);
            lastOffset = offidx;

            String key = arguments[i];
            String obj;
            if (!key.isEmpty()) {
                obj = formatObject(processKey(key));
            } else {
                // else just copy the left and right braces
                result.append(this.ldel);
                result.append(this.rdel);
                continue;
            }

            if (obj == null) {
                // try less-greedy match; useful for e.g. PROP___PROPNAME__ where
                // 'PROPNAME' is a key and delims are both '__'
                // this does not solve all possible cases, surely, but it should catch
                // the most common ones
                String lessgreedy = ldel + key;
                int fromright = lessgreedy.lastIndexOf(ldel);

                if (fromright > 0) {
                    String newkey = lessgreedy.substring(fromright + ldel.length());
                    String newsubst = formatObject(processKey(newkey));

                    if (newsubst != null) {
                        obj = lessgreedy.substring(0, fromright) + newsubst;
                    }
                }
            }

            if (obj == null) {
                if (throwex) {
                    throw new IllegalArgumentException("ObjectForKey");
                } else {
                    obj = ldel + key + rdel;
                }
            }

            result.append(obj);
        }

        result.append(pattern.substring(lastOffset));

        return result;
    }

    /**
     * Parses the string. Does not yet handle recursion (where
     * the substituted strings contain %n references.)
     */
    public Object parseObject(String text, ParsePosition status) {
        return parse(text);
    }

    /**
     * Parses the string. Does not yet handle recursion (where
     * the substituted strings contain {n} references.)
     *
     * @return New format.
     */
    public String parse(String source) {
        StringBuilder sbuf = new StringBuilder(source);

        //skipped = new RangeList();
        // What was this for??
        //process(source, "\"", "\""); // NOI18N
        for (Object object : argmap.keySet()) {
            String it_key = (String) object;
            String it_obj = formatObject(argmap.get(it_key));
            int it_idx = -1;

            do {
                it_idx = sbuf.toString().indexOf(it_obj, ++it_idx);

                if (it_idx >= 0 /* && !skipped.containsOffset(it_idx) */) {
                    sbuf.replace(it_idx, it_idx + it_obj.length(), ldel + it_key + rdel);

                    //skipped = new RangeList();
                    // What was this for??
                    //process(sbuf.toString(), "\"", "\""); // NOI18N
                }
            } while (it_idx != -1);
        }

        return sbuf.toString();
    }

    /**
     * Test whether formatter will throw exception if object for key was not found.
     * If given map does not contain object for key specified, it could
     * throw an exception. Returns true if throws. If not, key is left unchanged.
     */
    public boolean willThrowExceptionIfKeyWasNotFound() {
        return throwex;
    }

    /**
     * Specify whether formatter will throw exception if object for key was not found.
     * If given map does not contain object for key specified, it could
     * throw an exception. If does not throw, key is left unchanged.
     *
     * @param flag If true, formatter throws IllegalArgumentException.
     */
    public void setThrowExceptionIfKeyWasNotFound(boolean flag) {
        throwex = flag;
    }

    /**
     * Test whether both brackets are required in the expression.
     * If not, use setExactMatch(false) and formatter will ignore missing right
     * bracket. Advanced feature.
     */
    public boolean isExactMatch() {
        return exactmatch;
    }

    /**
     * Specify whether both brackets are required in the expression.
     * If not, use setExactMatch(false) and formatter will ignore missing right
     * bracket. Advanced feature.
     *
     * @param flag If true, formatter will ignore missing right bracket (default = false)
     */
    public void setExactMatch(boolean flag) {
        exactmatch = flag;
    }

    /**
     * Returns string used as left brace
     */
    public String getLeftBrace() {
        return ldel;
    }

    /**
     * Sets string used as left brace
     *
     * @param delimiter Left brace.
     */
    public void setLeftBrace(String delimiter) {
        ldel = delimiter;
    }

    /**
     * Returns string used as right brace
     */
    public String getRightBrace() {
        return rdel;
    }

    /**
     * Sets string used as right brace
     *
     * @param delimiter Right brace.
     */
    public void setRightBrace(String delimiter) {
        rdel = delimiter;
    }

    /**
     * Returns argument map
     */
    public Map getMap() {
        return argmap;
    }

    /**
     * Sets argument map
     * This map should contain key-value pairs with key values used in
     * formatted string expression. If value for key was not found, formatter leave
     * key unchanged (except if you've set setThrowExceptionIfKeyWasNotFound(true),
     * then it fires IllegalArgumentException.
     *
     * @param map the argument map
     */
    public void setMap(Map map) {
        argmap = map;
    }

    // commented out because unused --jglick

    /**
     * Range of expression in string.
     * Used internally to store information about quotation marks and comments
     * in formatted string.
     *
     * @author Slavek Psenicka
     * @version 1.0, March 11. 1999
     *
    class Range extends Object
    {
    /** Offset of expression *
    private int offset;

    /** Length of expression *
    private int length;

    /** Constructor *
    public Range(int off, int len)
    {
    offset = off;
    length = len;
    }

    /** Returns offset *
    public int getOffset()
    {
    return offset;
    }

    /** Returns length of expression *
    public int getLength()
    {
    return length;
    }

    /** Returns final position of expression *
    public int getEnd()
    {
    return offset+length;
    }

    public String toString()
    {
    return "("+offset+", "+length+")"; // NOI18N
    }
    }

    /**
     * List of ranges.
     * Used internally to store information about quotation marks and comments
     * in formatted string.
     *
     * @author Slavek Psenicka
     * @version 1.0, March 11. 1999
     *
    class RangeList
    {
    /** Map with Ranges *
    private HashMap hmap;

    /** Constructor *
    public RangeList()
    {
    hmap = new HashMap();
    }

    /** Returns true if offset is enclosed by any Range object in list *
    public boolean containsOffset(int offset)
    {
    return (getRangeContainingOffset(offset) != null);
    }

    /** Returns enclosing Range object in list for given offset *
    public Range getRangeContainingOffset(int offset)
    {
    if (hmap.size() == 0) return null;
    int offit = offset;
    while (offit-- >= 0) {
    Integer off = new Integer(offit);
    if (hmap.containsKey(off)) {
    Range ran = (Range)hmap.get(off);
    if (ran.getEnd() - offset > 0) return ran;
    }
    }

    return null;
    }

    /** Puts new range into list *
    public void put(Range range)
    {
    hmap.put(new Integer(range.getOffset()), range);
    }

    public String toString()
    {
    return hmap.toString();
    }
    }
     */
}
