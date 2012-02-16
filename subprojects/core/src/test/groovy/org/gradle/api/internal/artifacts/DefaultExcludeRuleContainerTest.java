/*
 * Copyright 2007-2008 the original author or authors.
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
package org.gradle.api.internal.artifacts;

import org.gradle.api.artifacts.ExcludeRule;
import org.gradle.util.WrapUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Hans Dockter
 */
public class DefaultExcludeRuleContainerTest {
    @Test
    public void testInit() {
        assertThat(new DefaultExcludeRuleContainer().getRules().size(), equalTo(0));
    }

    @Test
    public void testInitWithRules() {
        Set<ExcludeRule> sourceExcludeRules = new HashSet<ExcludeRule>();
        sourceExcludeRules.add(new DefaultExcludeRule(WrapUtil.toMap("group", "value")));
        DefaultExcludeRuleContainer defaultExcludeRuleContainer = new DefaultExcludeRuleContainer(sourceExcludeRules);
        assertThat(defaultExcludeRuleContainer.getRules(), equalTo(sourceExcludeRules));
        assertThat(defaultExcludeRuleContainer.getRules(), not(sameInstance(sourceExcludeRules)));
    }

    @Test
    public void testAdd() {
        DefaultExcludeRuleContainer excludeRuleContainer = new DefaultExcludeRuleContainer();
        Map<String, String> excludeRuleArgs1 = WrapUtil.toMap("group", "value1");
        Map<String, String> excludeRuleArgs2 = WrapUtil.toMap("module", "value2");
        excludeRuleContainer.add(excludeRuleArgs1);
        excludeRuleContainer.add(excludeRuleArgs2);
        assertThat(excludeRuleContainer.getRules().size(), equalTo(2));
        assertExcludeRuleContainerHasCorrectExcludeRules(excludeRuleContainer.getRules(), excludeRuleArgs1, excludeRuleArgs2);
    }

    @Test
    public void testAddIgnoresInvalidExcludeDefinition() {
        DefaultExcludeRuleContainer excludeRuleContainer = new DefaultExcludeRuleContainer();
        Map<String, String> excludeRuleArgs1 = WrapUtil.toMap("group", "value1");
        Map<String, String> excludeRuleArgs2 = WrapUtil.toMap("invalidkey2", "value2");
        excludeRuleContainer.add(excludeRuleArgs1);
        excludeRuleContainer.add(excludeRuleArgs2);
        assertThat(excludeRuleContainer.getRules().size(), equalTo(1));
        assertExcludeRuleContainerHasCorrectExcludeRules(excludeRuleContainer.getRules(), excludeRuleArgs1);
    }

    @Test
    public void testIsValidExcludeRuleChecksForExistingGroupOrModuleEntry() {
        DefaultExcludeRuleContainer excludeRuleContainer = new DefaultExcludeRuleContainer();
        Map<String, String> excludeRuleArgs1 = WrapUtil.toMap("group", "value1");
        Map<String, String> excludeRuleArgs2 = WrapUtil.toMap("module", "value1");
        Map<String, String> excludeRuleArgs3 = WrapUtil.toMap("invalidkey2", "value2");

        Map<String, String> combinedExcludeRuleArgs = new HashMap<String, String>();
        combinedExcludeRuleArgs.putAll(excludeRuleArgs1);
        combinedExcludeRuleArgs.putAll(excludeRuleArgs2);
        combinedExcludeRuleArgs.putAll(excludeRuleArgs3);

        assertTrue(excludeRuleContainer.isValidExcludeRule(excludeRuleArgs1));
        assertTrue(excludeRuleContainer.isValidExcludeRule(excludeRuleArgs2));
        assertFalse(excludeRuleContainer.isValidExcludeRule(excludeRuleArgs3));
        assertTrue(excludeRuleContainer.isValidExcludeRule(combinedExcludeRuleArgs));
    }

    private void assertExcludeRuleContainerHasCorrectExcludeRules(Set<ExcludeRule> excludeRules, Map... excludeRuleArgs) {
        Set<Map> foundRules = new HashSet<Map>();
        for (ExcludeRule excludeRule : excludeRules) {
            for (Map excludeRuleArg : excludeRuleArgs) {
                if (excludeRule.getExcludeArgs().equals(excludeRuleArg)) {
                    foundRules.add(excludeRuleArg);
                    continue;
                }
            }
        }
    }
}