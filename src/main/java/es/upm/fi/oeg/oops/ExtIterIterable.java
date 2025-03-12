/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.Iterator;
import org.apache.jena.util.iterator.ExtendedIterator;

public class ExtIterIterable<T> implements Iterable<T> {

    private final ExtendedIterator<T> inner;

    public ExtIterIterable(final ExtendedIterator<T> inner) {
        this.inner = inner;
    }

    private class ExtIterIterator implements Iterator<T> {
        @Override
        public boolean hasNext() {
            return inner.hasNext();
        }

        @Override
        public T next() {
            return inner.next();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new ExtIterIterator();
    }
}
