/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.integtests.tooling

import org.gradle.integtests.fixtures.GradleDistribution
import org.gradle.integtests.tooling.fixture.ToolingApi
import org.gradle.tooling.model.idea.IdeaProject
import org.gradle.util.ConcurrentSpecification
import spock.lang.Issue

class ConcurrentToolingApiIntegrationTest extends ConcurrentSpecification {

    def dist = new GradleDistribution()
    def toolingApi = new ToolingApi(dist)

//    @Ignore
    @Issue("GRADLE-1933")
    def "handles concurrent scenario"() {
        //temporary hack to only run in embedded mode. Useful for local testin
        toolingApi.isEmbedded = false

        dist.file('build.gradle')  << """
apply plugin: 'java'
        """

        when:
        shortTimeout = 50000

        5.times { executor.execute(thread()) }

        then:
        //fails most of the time
        finished()
    }

    private Thread thread() {
        def thread = new Thread() {
            void run() {
                toolingApi.withConnection {
                    try {
                        def model = it.getModel(IdeaProject)
                        assert model != null
                    } catch (Exception e) {
                        throw new RuntimeException("""Looks like we've hit a concurrency problem.
See the full stacktrace and the list of causes to investigate""", e);
                    }
                }
            }
        }
        return thread
    }
}
