/*
 * <<
 *  Davinci
 *  ==
 *  Copyright (C) 2016 - 2019 EDP
 *  ==
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  >>
 *
 */

package com.webank.wedatasphere.qualitis.common;

/**
 * 常量
 */
public class Constants {

    public static final String EMPTY = "";

    /**
     * Token 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer";

    /**
     * Token header名称
     */
    public static final String TOKEN_HEADER_STRING = "Authorization";


    public static final String USER_TICKET_ID_STRING = "bdp-user-ticket-id";


    public static boolean isEmpty(String value) {
        return isEmpty((CharSequence)value);
    }

    public static boolean isEmpty(CharSequence value) {
        return value == null || value.length() == 0;
    }
}
