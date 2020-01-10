package org.mongojack.internal.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.Block;
import com.mongodb.Function;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.MapReduceAction;
import com.mongodb.lang.Nullable;
import org.bson.conversions.Bson;
import org.mongojack.JacksonCodecRegistry;

import java.util.Collection;
import java.util.Spliterator;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Wraps a MapReduceIterable so we can map the incoming filters.
 *
 * @param <TResult>
 */
public class MapReduceIterableDecorator<TResult> implements MapReduceIterable<TResult> {

    private final MapReduceIterable<TResult> delegate;
    private final ObjectMapper objectMapper;
    private final JavaType type;
    private final JacksonCodecRegistry codecRegistry;

    public MapReduceIterableDecorator(final MapReduceIterable<TResult> delegate, final ObjectMapper objectMapper, final JavaType type, final JacksonCodecRegistry codecRegistry) {
        this.delegate = delegate;
        this.objectMapper = objectMapper;
        this.type = type;
        this.codecRegistry = codecRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toCollection() {
        delegate.toCollection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> collectionName(final String collectionName) {
        return delegate.collectionName(collectionName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> finalizeFunction(final String finalizeFunction) {
        return delegate.finalizeFunction(finalizeFunction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> scope(final Bson scope) {
        return delegate.scope(scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> sort(final Bson sort) {
        return delegate.sort(sort);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> filter(final Bson filter) {
        if (filter instanceof InitializationRequiredForTransformation) {
            ((InitializationRequiredForTransformation) filter).initialize(objectMapper, type, codecRegistry);
            return delegate.filter(filter);
        }
        return delegate.filter(DocumentSerializationUtils.serializeFilter(objectMapper, type, filter, codecRegistry));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> limit(final int limit) {
        return delegate.limit(limit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> jsMode(final boolean jsMode) {
        return delegate.jsMode(jsMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> verbose(final boolean verbose) {
        return delegate.verbose(verbose);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> maxTime(final long maxTime, final TimeUnit timeUnit) {
        return delegate.maxTime(maxTime, timeUnit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> action(final MapReduceAction action) {
        return delegate.action(action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> databaseName(final String databaseName) {
        return delegate.databaseName(databaseName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> sharded(final boolean sharded) {
        return delegate.sharded(sharded);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> nonAtomic(final boolean nonAtomic) {
        return delegate.nonAtomic(nonAtomic);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> batchSize(final int batchSize) {
        return delegate.batchSize(batchSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> bypassDocumentValidation(final Boolean bypassDocumentValidation) {
        return delegate.bypassDocumentValidation(bypassDocumentValidation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapReduceIterable<TResult> collation(final Collation collation) {
        return delegate.collation(collation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoCursor<TResult> iterator() {
        return delegate.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoCursor<TResult> cursor() {
        return delegate.cursor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public TResult first() {
        return delegate.first();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> MongoIterable<U> map(final Function<TResult, U> mapper) {
        return delegate.map(mapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public void forEach(final Block<? super TResult> block) {
        delegate.forEach(block);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A extends Collection<? super TResult>> A into(final A target) {
        return delegate.into(target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void forEach(final Consumer<? super TResult> action) {
        delegate.forEach(action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Spliterator<TResult> spliterator() {
        return delegate.spliterator();
    }
}
