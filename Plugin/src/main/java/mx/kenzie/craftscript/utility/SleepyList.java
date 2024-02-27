package mx.kenzie.craftscript.utility;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Semaphore;

public class SleepyList<Type> extends AbstractCollection<Type> implements List<Type> {

    protected final List<Type> list;
    protected final Semaphore semaphore;

    protected SleepyList(List<Type> list) {
        this.list = list;
        this.semaphore = new Semaphore(1);
    }

    public SleepyList() {
        this(new ArrayList<>());
    }

    public SleepyList(Collection<? extends Type> collection) {
        this(new ArrayList<>(collection));
    }

    @Override
    public Iterator<Type> iterator() {
        return this.listIterator();
    }

    @Override
    public int size() {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted.", ex);
        }
        try {
            return list.size();
        } finally {
            this.semaphore.release();
        }
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends Type> c) {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted.", ex);
        }
        try {
            return list.addAll(index, c);
        } finally {
            this.semaphore.release();
        }
    }

    @Override
    public Type get(int index) {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted.", ex);
        }
        try {
            return list.get(index);
        } finally {
            this.semaphore.release();
        }
    }

    @Override
    public Type set(int index, Type element) {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted.", ex);
        }
        try {
            return list.set(index, element);
        } finally {
            this.semaphore.release();
        }
    }

    @Override
    public void add(int index, Type element) {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted.", ex);
        }
        try {
            this.list.add(index, element);
        } finally {
            this.semaphore.release();
        }

    }

    @Override
    public Type remove(int index) {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted.", ex);
        }
        try {
            return list.remove(index);
        } finally {
            this.semaphore.release();
        }
    }

    @Override
    public int indexOf(Object o) {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted.", ex);
        }
        try {
            return list.indexOf(o);
        } finally {
            this.semaphore.release();
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted.", ex);
        }
        try {
            return list.lastIndexOf(o);
        } finally {
            this.semaphore.release();
        }
    }

    @NotNull
    @Override
    public ListIterator<Type> listIterator() {
        return new LoopyLoop(0);
    }

    @NotNull
    @Override
    public ListIterator<Type> listIterator(int index) {
        return new LoopyLoop(index);
    }

    @NotNull
    @Override
    public SleepyList<Type> subList(int fromIndex, int toIndex) {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted.", ex);
        }
        try {
            return new SleepyList<>(list.subList(fromIndex, toIndex));
        } finally {
            this.semaphore.release();
        }
    }

    protected class LoopyLoop implements ListIterator<Type> {

        protected int counter;

        public LoopyLoop(int start) {
            this.counter = start;
        }

        @Override
        public boolean hasNext() {
            return list.size() > this.nextIndex();
        }

        @Override
        public Type next() {
            return this.get(counter++);
        }

        @Override
        public boolean hasPrevious() {
            return counter > 0;
        }

        @Override
        public Type previous() {
            return this.get(--counter);
        }

        @Override
        public int nextIndex() {
            return counter + 1;
        }

        @Override
        public int previousIndex() {
            return counter;
        }

        @Override
        public void remove() {
            try {
                semaphore.acquire();
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted.", ex);
            }
            try {
                list.remove(counter);
            } finally {
                semaphore.release();
            }
        }

        @Override
        public void set(Type type) {
            try {
                semaphore.acquire();
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted.", ex);
            }
            try {
                list.set(counter, type);
            } finally {
                semaphore.release();
            }
        }

        @Override
        public void add(Type type) {
            try {
                semaphore.acquire();
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted.", ex);
            }
            try {
                list.add(counter, type);
            } finally {
                semaphore.release();
            }
        }

        protected Type get(int index) {
            try {
                semaphore.acquire();
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted.", ex);
            }
            try {
                return list.get(index);
            } finally {
                semaphore.release();
            }
        }

    }

}
