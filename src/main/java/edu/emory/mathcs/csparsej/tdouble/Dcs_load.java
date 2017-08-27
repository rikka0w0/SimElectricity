/* ***** BEGIN LICENSE BLOCK *****
 *
 * CSparse: a Concise Sparse matrix package.
 * Copyright (c) 2006, Timothy A. Davis.
 * http://www.cise.ufl.edu/research/sparse/CSparse
 *
 * -------------------------------------------------------------------------
 *
 * CSparseJ is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * CSparseJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this Module; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * ***** END LICENSE BLOCK ***** */

package edu.emory.mathcs.csparsej.tdouble;

import java.io.*;

/**
 * Load a sparse matrix from a file.
 *
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 */
public class Dcs_load {

    /**
     * Loads a triplet matrix T from a file. Each line of the file contains
     * three values: a row index i, a column index j, and a numerical value aij.
     *
     * @param fileName file name
     * @param base     index base
     * @return T if successful, null on error
     */
    public static Dcs_common.Dcs cs_load(InputStream in, int base) {
        int i, j;
        double x;
        Dcs_common.Dcs T;
        Reader r = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(r);

        T = Dcs_util.cs_spalloc(0, 0, 1, true, true); /* allocate result */
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");
                if (tokens.length != 3) {
                    return null;
                }
                i = Integer.parseInt(tokens[0]) - base;
                j = Integer.parseInt(tokens[1]) - base;
                x = Double.parseDouble(tokens[2]);
                if (!Dcs_entry.cs_entry(T, i, j, x))
                    return null;
            }
            r.close();
            br.close();
        } catch (IOException e) {
            return null;
        }
        return T;
    }

    /**
     * Loads a triplet matrix T from a file. The file is zero-based.
     */
    public static Dcs_common.Dcs cs_load(InputStream in) {
        return Dcs_load.cs_load(in, 0);
    }
}
