/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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


package org.slc.sli.validation.strategy;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * Validation strategy to check for invalid characters in String input
 * @author vmcglaughlin
 */
@Component
public class CharacterBlacklistStrategy extends AbstractBlacklistStrategy {

    private Set<Character> characterSet;

    /**
     * Default constructor, sets identifier to "CharacterBlacklistStrategy"
     */
    public CharacterBlacklistStrategy() {
        super("CharacterBlacklistStrategy");
    }

    @Override
    @PostConstruct
    protected void init() {
        characterSet = new HashSet<Character>();

        if (inputCollection == null) {
            return;
        }

        for (String entry : inputCollection) {
            if (entry.isEmpty()) {
                continue;
            }

            try {
                String charStr = entry;
                if (entry.startsWith("\\u")) {
                    charStr = entry.substring(2);
                }

                char c = (char) Integer.parseInt(charStr, 16);
                characterSet.add(Character.valueOf(c));

            } catch (NumberFormatException e) {
                continue;
            }
        }
    }

    @Override
    public boolean isValid(String context, String input) {
        if (input == null) {
            return false;
        }

        for (char c : input.toCharArray()) {
            if (characterSet.contains(c)) {
                return false;
            }
        }
        return true;
    }
}
