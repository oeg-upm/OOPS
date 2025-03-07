#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
# SPDX-License-Identifier: CC0-1.0
#
# See the output of "$0 -h" for details.

# Exit immediately on each error and unset variable;
# see: https://vaneyckt.io/posts/safer_bash_scripts_with_set_euxo_pipefail/
set -Eeuo pipefail
# /\ BASH || \/ sh
#set -Eeu

script_path="$(readlink -f "${BASH_SOURCE[0]}")"
#script_dir="$(dirname "$script_path")"
script_name="$(basename "$script_path")"

function print_help() {

	echo "Runs all pitfall test ontologies through the CLI."
	echo
	echo "Usage:"
	echo "  $script_name [OPTION...]"
	echo "Options:"
	echo "  -h, --help"
	echo "    Print this usage help and exits"
}

# Process command line arguments
while [[ $# -gt 0 ]]
do
	arg="$1"
	shift # $2 -> $1, $3 -> $2, ...

	case "$arg" in
		-h|--help)
			print_help
			exit 0
			;;
		*) # non-/unknown option
			>&2 echo "Unknown flag: '$arg'"
			exit 1
			;;
	esac
done

num_tests="$(find src/test/resources/data/ -name "P*.owl" | wc -l)"
idx_test=0
for test_ont_file in src/test/resources/data/P*.owl
do
	idx_test=$((idx_test+1))
	test_name="$(basename "$test_ont_file")"
	>&2 printf "\n%s Running test %d/%d\t- %s ..." \
		"INFO" "$idx_test" "$num_tests" "$test_name"
	mvn exec:java -D exec.args="--input-file \"$test_ont_file\""
done

>&2 echo -e "\nINFO done."
