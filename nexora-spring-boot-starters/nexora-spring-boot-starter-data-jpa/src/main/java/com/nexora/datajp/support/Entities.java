package com.nexora.datajp.support;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Fluent entity operations - DSL style.
 *
 * <h2>Create</h2>
 * <pre>{@code
 * // with input
 * Entities.create(repo)
 *     .with(input, MyEntity::of)
 *     .execute();
 *
 * // without input
 * Entities.create(repo)
 *     .supply(MyEntity::new)
 *     .execute();
 * }</pre>
 *
 * <h2>Update</h2>
 * <pre>{@code
 * // with input
 * Entities.update(repo, id)
 *     .with(input, (entity, i) -> entity.apply(i))
 *     .execute();
 *
 * // without input
 * Entities.update(repo, id)
 *     .apply(entity -> entity.touch())
 *     .execute();
 * }</pre>
 *
 * @author sujie
 * @since 1.0.0
 */
public final class Entities {

    private Entities() {
    }

    /**
     * Start create operation.
     */
    public static <T> CreateStart<T> create(JpaRepository<T, Long> repo) {
        return new CreateStart<>(repo);
    }

    /**
     * Start update operation.
     */
    public static <T> UpdateStart<T> update(JpaRepository<T, Long> repo, Long id) {
        return new UpdateStart<>(repo, id);
    }

    /**
     * Delete entity by ID.
     */
    public static <T> boolean delete(JpaRepository<T, Long> repo, Long id) {
        if (!repo.existsById(id)) {
            return false;
        }
        repo.deleteById(id);
        return true;
    }

    /**
     * Create operation start.
     */
    public static class CreateStart<T> {

        private final JpaRepository<T, Long> repo;

        CreateStart(JpaRepository<T, Long> repo) {
            this.repo = repo;
        }

        /**
         * Transform input to entity.
         */
        public <I> CreateWith<T> with(I input, Function<I, T> mapper) {
            return new CreateWith<>(repo, mapper, input);
        }

        /**
         * Supply entity instance.
         */
        public CreateSupply<T> supply(Supplier<T> supplier) {
            return new CreateSupply<>(repo, supplier);
        }
    }

    /**
     * Create with input and mapper.
     */
    public static class CreateWith<T> {

        private final JpaRepository<T, Long> repo;
        private final Function<Object, T> mapper;
        private final Object input;

        @SuppressWarnings("unchecked")
        <I> CreateWith(JpaRepository<T, Long> repo, Function<I, T> mapper, I input) {
            this.repo = repo;
            this.mapper = (Function<Object, T>) mapper;
            this.input = input;
        }

        /**
         * Execute the create operation.
         */
        public Optional<T> execute() {
            return Optional.ofNullable(mapper.apply(input)).map(repo::save);
        }
    }

    /**
     * Create with supplier.
     */
    public static class CreateSupply<T> {

        private final JpaRepository<T, Long> repo;
        private final Supplier<T> supplier;

        CreateSupply(JpaRepository<T, Long> repo, Supplier<T> supplier) {
            this.repo = repo;
            this.supplier = supplier;
        }

        /**
         * Execute the create operation.
         */
        public Optional<T> execute() {
            return Optional.ofNullable(supplier.get()).map(repo::save);
        }
    }

    /**
     * Update operation start.
     */
    public static class UpdateStart<T> {

        private final JpaRepository<T, Long> repo;
        private final Long id;

        UpdateStart(JpaRepository<T, Long> repo, Long id) {
            this.repo = repo;
            this.id = id;
        }

        /**
         * Apply update with input.
         */
        public <I> UpdateWith<T> with(I input, BiFunction<T, I, T> updater) {
            return new UpdateWith<>(repo, id, updater, input);
        }

        /**
         * Apply update without input.
         */
        public UpdateApply<T> apply(Function<T, T> updater) {
            return new UpdateApply<>(repo, id, updater);
        }
    }

    /**
     * Update with input.
     */
    public static class UpdateWith<T> {

        private final JpaRepository<T, Long> repo;
        private final Long id;
        private final BiFunction<T, Object, T> updater;
        private final Object input;

        @SuppressWarnings("unchecked")
        <I> UpdateWith(JpaRepository<T, Long> repo, Long id, BiFunction<T, I, T> updater, I input) {
            this.repo = repo;
            this.id = id;
            this.updater = (BiFunction<T, Object, T>) updater;
            this.input = input;
        }

        /**
         * Execute the update operation.
         */
        public Optional<T> execute() {
            return repo.findById(id)
                .map(entity -> updater.apply(entity, input))
                .map(repo::save);
        }
    }

    /**
     * Update without input.
     */
    public static class UpdateApply<T> {

        private final JpaRepository<T, Long> repo;
        private final Long id;
        private final Function<T, T> updater;

        UpdateApply(JpaRepository<T, Long> repo, Long id, Function<T, T> updater) {
            this.repo = repo;
            this.id = id;
            this.updater = updater;
        }

        /**
         * Execute the update operation.
         */
        public Optional<T> execute() {
            return repo.findById(id)
                .map(updater)
                .map(repo::save);
        }
    }
}
