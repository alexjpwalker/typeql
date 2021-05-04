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

package com.vaticle.typeql.lang.pattern;

import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.CURLY_CLOSE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.CURLY_OPEN;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Operator.OR;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class Disjunction<T extends Pattern> implements Pattern {

    private final List<T> patterns;
    private final int hash;
    private Disjunction<Conjunction<Conjunctable>> normalised;

    public Disjunction(List<T> patterns) {
        if (patterns == null) throw new NullPointerException("Null patterns");
        this.patterns = patterns.stream().map(Objects::requireNonNull).collect(toList());
        this.hash = Objects.hash(this.patterns);
    }

    @Override
    public List<T> patterns() {
        return patterns;
    }

    @Override
    public void validateIsBoundedBy(Set<UnboundVariable> bounds) {
        patterns.forEach(pattern -> pattern.validateIsBoundedBy(bounds));
    }

    @Override
    public Disjunction<Conjunction<Conjunctable>> normalise() {
        if (normalised == null) {
            List<Conjunction<Conjunctable>> conjunctions = patterns.stream().flatMap(p -> {
                if (p.isVariable()) return Stream.of(new Conjunction<>(list(p.asConjunctable())));
                else if (p.isNegation())
                    return Stream.of(new Conjunction<>(list(p.asNegation().normalise().asConjunctable())));
                else if (p.isConjunction()) return p.asConjunction().normalise().patterns().stream();
                else return p.asDisjunction().normalise().patterns().stream();
            }).collect(toList());
            normalised = new Disjunction<>(conjunctions);
        }
        return normalised;
    }

    @Override
    public boolean isDisjunction() { return true; }

    @Override
    public Disjunction<?> asDisjunction() { return this; }

    @Override
    public String toString() {
        StringBuilder syntax = new StringBuilder();

        Iterator<T> patternIter = patterns.iterator();
        while (patternIter.hasNext()) {
            Pattern pattern = patternIter.next();
            syntax.append(CURLY_OPEN).append(SPACE);

            if (pattern.isConjunction()) {
                Stream<? extends Pattern> patterns = pattern.asConjunction().patterns().stream();
                syntax.append(patterns.map(Object::toString).collect(joining("" + SEMICOLON + SPACE)));
            } else {
                syntax.append(pattern);
            }
            syntax.append(SEMICOLON).append(SPACE).append(CURLY_CLOSE);
            if (patternIter.hasNext()) syntax.append(SPACE).append(OR).append(SPACE);
        }
        return syntax.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Disjunction<?> that = (Disjunction<?>) o;
        return Objects.equals(patterns, that.patterns);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
