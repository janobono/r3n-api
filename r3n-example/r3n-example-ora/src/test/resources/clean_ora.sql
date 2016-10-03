BEGIN

	FOR c IN (SELECT view_name FROM user_views) LOOP
		EXECUTE IMMEDIATE ('DROP VIEW ' || c.view_name || ' CASCADE CONSTRAINTS')\;
	END LOOP\;

	FOR c IN (SELECT table_name FROM user_tables) LOOP
		EXECUTE IMMEDIATE ('DROP TABLE ' || c.table_name || ' CASCADE CONSTRAINTS')\;
	END LOOP\;
	
	FOR s IN (SELECT sequence_name FROM user_sequences) LOOP
		EXECUTE IMMEDIATE ('DROP SEQUENCE ' || s.sequence_name)\;
	END LOOP\;

END\;
;
