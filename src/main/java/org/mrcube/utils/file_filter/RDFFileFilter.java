/*
 * Project Name: MR^3 (Meta-Model Management based on RDFs Revision Reflection)
 * Project Website: http://mrcube.org/
 *
 * Copyright (C) 2003-2020 Takeshi Morita. All rights reserved.
 *
 * This file is part of MR^3.
 *
 * MR^3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MR^3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MR^3.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.mrcube.utils.file_filter;

import java.io.File;

/**
 * @author Takeshi Morita
 */
public class RDFFileFilter extends MR3FileFilter implements java.io.FileFilter {

    private final boolean isShowDirectories;

    public RDFFileFilter(boolean isShowDirectories) {
        this.isShowDirectories = isShowDirectories;
    }

    public String getExtension() {
        return "rdf";
    }

    public boolean accept(File f) {
        if (f.isDirectory()) {
            return isShowDirectories;
        }
        String extension = getExtension(f);
        return extension != null && extension.equals("rdf");
    }

    public String getDescription() {
        return "RDF/XML (*.rdf)";
    }
}
