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

package org.deri.any23.plugin.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import org.deri.any23.Any23OnlineTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Test case for {@link SiteCrawler}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class SiteCrawlerTest extends Any23OnlineTestBase {

    public static final Logger logger = LoggerFactory.getLogger(SiteCrawlerTest.class);

    /**
     * Tests the main crawler use case.
     *
     * @throws Exception
     */
    @Test
    public void testSiteCrawling() throws Exception {
        assumeOnlineAllowed();

        File tmpFile = File.createTempFile("site-crawler-test", ".storage");
        tmpFile.delete();

        final SiteCrawler controller = new SiteCrawler(tmpFile);
        controller.setMaxPages(100);
        controller.setPolitenessDelay(500);

        final Set<String> distinctPages = new HashSet<String>();
        controller.addListener(new CrawlerListener() {
            @Override
            public void visitedPage(Page page) {
                distinctPages.add( page.getWebURL().getURL() );
            }
        });

        controller.start( new URL("http://schema.org/"), false);

        synchronized (this) {
            this.wait(15 * 1000);
        }
        controller.stop();

        logger.debug("Crawled pages: " + distinctPages.size());
        Assert.assertTrue("Expected some page crawled.", distinctPages.size() > 0);
    }

}
