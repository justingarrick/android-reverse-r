/*
 * Copyright (c) 2015 Justin Garrick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.justingarrick.reverser.cli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Parses and wraps command line arguments.
 */
public class Settings {

    /** The directory containing all of the .java source code. */
    @Parameter(names= "-src",
            description = "Directory containing source code",
            converter = DirectoryConverter.class,
            required = true)
    private Path source = null;

    /** The fully qualified package name of the R.java file to be reversed. */
    @Parameter(names = "-pkg",
            description = "Package name of R.java file(s) to reverse",
            required = true)
    private String packageName = null;


    /** The help parameter, displays usage. */
    @Parameter(names = "--help",
            help = true)
    private boolean help = false;

    /**
     * Get the source path.
     *
     * @return the source path
     */
    public Path getSource() {
        return source;
    }

    /**
     * Get the package name.
     *
     * @return the package name
     */
    public String getPackage() {
        return packageName;
    }

    /**
     * Is this a help/usage request?
     *
     * @return true if this is a help request, false otherwise
     */
    public boolean isHelp() {
        return help;
    }

    /**
     * A converter that transforms a String into a Path.
     */
    private static class PathConverter implements IStringConverter<Path> {
        @Override
        public Path convert(String value) {
            return Paths.get(value);
        }
    }

    /**
     * A converter that transforms a String into a directory.
     * This ensures that the directory exists and is writeable,
     * or creates a new directory and all parents.
     */
    public static final class DirectoryConverter extends PathConverter {
        @Override
        public Path convert(String value) {
            Path dir = super.convert(value);
            if (Files.exists(dir)) {
                if (!Files.isDirectory(dir) || !Files.isWritable(dir))
                    throw new ParameterException(value + " is not a writeable directory");
            } else {
                try {
                    Files.createDirectories(dir);
                } catch (IOException e) {
                    throw new ParameterException("Failed to create directory at " + value);
                }
            }
            return dir;
        }
    }
}
