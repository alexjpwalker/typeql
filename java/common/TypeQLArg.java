/*
 * Copyright (C) 2021 Vaticle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.vaticle.typeql.lang.common;

public class TypeQLArg {

    public enum QueryType {
        READ(0),
        WRITE(1);

        private final int id;
        private final boolean isWrite;

        QueryType(int id) {
            this.id = id;
            this.isWrite = id == 1;
        }

        public static QueryType of(int value) {
            for (QueryType t : values()) {
                if (t.id == value) return t;
            }
            return null;
        }

        public boolean isRead() { return !isWrite; }

        public boolean isWrite() { return isWrite; }
    }

    public enum ValueType {
        BOOLEAN("boolean"),
        DATETIME("datetime"),
        DOUBLE("double"),
        LONG("long"),
        STRING("string");

        private final String type;

        ValueType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return this.type;
        }

        public static ValueType of(String value) {
            for (ValueType c : ValueType.values()) {
                if (c.type.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    public enum Order {
        ASC("asc"),
        DESC("desc");

        private final String order;

        Order(String order) {
            this.order = order;
        }

        @Override
        public String toString() {
            return this.order;
        }

        public static Order of(String value) {
            for (Order c : Order.values()) {
                if (c.order.equals(value)) {
                    return c;
                }
            }
            return null;
        }
    }

    /**
     * TypeQL Compute algorithm names
     */
    public enum Algorithm {
        DEGREE("degree"),
        K_CORE("k-core"),
        CONNECTED_COMPONENT("connected-component");

        private final String algorithm;

        Algorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        @Override
        public String toString() {
            return this.algorithm;
        }

        public static Algorithm of(String value) {
            for (Algorithm a : Algorithm.values()) {
                if (a.algorithm.equals(value)) {
                    return a;
                }
            }
            return null;
        }
    }
}
