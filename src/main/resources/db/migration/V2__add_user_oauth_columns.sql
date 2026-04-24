ALTER TABLE `user`
    ADD COLUMN provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    ADD COLUMN provider_external_id VARCHAR(100) NULL;

ALTER TABLE `user`
    ADD CONSTRAINT uk_user_provider_external UNIQUE (provider, provider_external_id);
