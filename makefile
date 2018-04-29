cloc:
	cloc \
		--not-match-d=^\\. \
		--not-match-f=gradle \
		--exclude-dir=node_modules,googletest,build,dist,.idea \
		.
