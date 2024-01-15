CREATE TABLE "crypto_scam_event" (
event_id serial PRIMARY KEY,
title varchar(25) NOT NULL,
description varchar(2000) NOT NULL,
other_reference_url varchar(100),
is_active boolean DEFAULT TRUE,
tags text ARRAY[5]
);
