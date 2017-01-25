ALTER TABLE community DROP COLUMN IF EXISTS server;

ALTER TABLE community ADD server character varying(100);