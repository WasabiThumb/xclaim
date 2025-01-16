package io.github.wasabithumb.xclaim.placeholder;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.placeholder.impl.*;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderRegistry {

    protected final XClaim runtime;
    private final Node root;

    public PlaceholderRegistry(@NotNull XClaim runtime) {
        this.runtime = runtime;
        this.root = new Node(16);
        this.registerDefaults();
    }

    //

    public @Nullable Placeholder get(@NotNull String key) {
        return this.root.get(PlaceholderArgumentQueue.of(key));
    }

    public @Nullable String resolve(@NotNull PlatformUser user, @NotNull PlaceholderArgumentQueue args) {
        Placeholder placeholder = this.root.get(args);
        if (placeholder == null) return null;
        return placeholder.resolve(user, args);
    }

    //

    protected void register(@NotNull Placeholder placeholder) {
        PlaceholderArgumentQueue key = PlaceholderArgumentQueue.of(placeholder.key());
        this.root.put(key, placeholder);
    }

    @SafeVarargs
    protected final void register(@NotNull Class<? extends Placeholder> @NotNull ... classes) {
        for (Class<? extends Placeholder> cls : classes) {
            Placeholder instance;
            try {
                instance = cls.getConstructor(XClaim.class).newInstance(this.runtime);
            } catch (ReflectiveOperationException | SecurityException e) {
                throw new AssertionError("Failed to instatiate placeholder \"" + cls.getName() + "\"", e);
            }
            this.register(instance);
        }
    }

    protected void registerDefaults() {
        this.register(
                ChunkCountInWorldPlaceholder.class,
                ChunkCountPlaceholder.class,
                ChunkMaxAbsPlaceholder.class,
                ChunkMaxPlaceholder.class,
                ClaimCountInWorldPlaceholder.class,
                ClaimCountPlaceholder.class,
                ClaimMaxPlaceholder.class
        );
    }

    //

    private record Entry(
            @NotNull CharSequence key,
            @NotNull Node value
    ) { }

    private static final class Node {

        private int capacity;
        private int length;
        private Entry[] children;
        private Placeholder leaf;

        Node(int initialCapacity) {
            this.capacity = initialCapacity;
            this.length = 0;
            this.children = new Entry[initialCapacity];
            this.leaf = null;
        }

        Node() {
            this(1);
        }

        @Nullable Placeholder get(@NotNull PlaceholderArgumentQueue key) {
            CharSequence first = key.poll();
            if (first == null) return this.leaf;

            Entry entry;
            for (int i=0; i < this.length; i++) {
                entry = this.children[i];
                if (CharSequence.compare(entry.key, first) == 0) {
                    return entry.value.get(key);
                }
            }

            key.rewind();
            return this.leaf;
        }

        void put(@NotNull PlaceholderArgumentQueue key, @NotNull Placeholder value) {
            CharSequence first = key.poll();
            if (first == null) {
                this.leaf = value;
                return;
            }
            int idx = -1;
            for (int i=0; i < this.length; i++) {
                if (CharSequence.compare(this.children[i].key, first) == 0) {
                    idx = i;
                    break;
                }
            }
            Node node;
            if (idx == -1) {
                idx = this.length++;
                if (this.length > this.capacity) {
                    int newCapacity = (int) Math.ceil(this.length / 0.75d);
                    Entry[] cpy = new Entry[newCapacity];
                    System.arraycopy(this.children, 0, cpy, 0, this.capacity);
                    this.children = cpy;
                    this.capacity = newCapacity;
                }
                node = new Node();
                this.children[idx] = new Entry(first, node);
            } else {
                node = this.children[idx].value;
            }
            node.put(key, value);
        }

    }

}
