/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.cli;

import org.junit.Test;

/**
 * Test case for {@link MicrodataParser} CLI.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MicrodataParserTest extends ToolTestBase {

    public MicrodataParserTest() {
        super(MicrodataParser.class);
    }

    @Test
    public void testRun() throws Exception {
        runToolCheckExit0("file:src/test/resources/microdata/microdata-nested.html");
    }

}
