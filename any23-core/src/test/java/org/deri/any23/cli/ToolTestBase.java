/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.deri.any23.cli;

import org.deri.any23.Any23OnlineTestBase;

import java.lang.reflect.Method;

/**
 * Base class for <i>CLI</i> related tests.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public abstract class ToolTestBase extends Any23OnlineTestBase {

    public static final String TOOL_RUN_METHOD = "run";

    private final Class<? extends Tool> toolClazz;

    protected ToolTestBase(Class<? extends Tool> tool) {
        if(tool == null) throw new NullPointerException();
        toolClazz = tool;
    }

    /**
     * Runs the underlying tool.
     *
     * @param args tool arguments.
     * @throws Exception
     * @return the tool exit code.
     */
    protected int runTool(String... args)
    throws Exception {
        final Object instance = toolClazz.newInstance();
        final Method mainMethod = toolClazz.getMethod(TOOL_RUN_METHOD, String[].class);
        return (Integer) mainMethod.invoke(instance, (Object) args);
    }

    /**
     * Runs the underlying tool.
     *
     * @param args args tool arguments.
     * @throws Exception
     * @return the tool exit code.
     */
    protected int runTool(String args) throws Exception {
        return runTool(args.split(" "));
    }

}
