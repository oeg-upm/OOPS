#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
# SPDX-License-Identifier: Apache-2.0
#
# See the output of "$0 -h" for details.

# Exit immediately on each error and unset variable;
# see: https://vaneyckt.io/posts/safer_bash_scripts_with_set_euxo_pipefail/
set -Eeuo pipefail
# /\ BASH || \/ sh
#set -Eeu

script_path="$(readlink -f "${BASH_SOURCE[0]}")"
script_dir="$(dirname "$script_path")"
script_name="$(basename "$script_path")"

# parameters
tomcat_webapps_root="/var/lib/tomcat10/webapps"
webapp_oops_root="$tomcat_webapps_root/oops-2.0.0-SNAPSHOT"

function print_help() {

	echo "Deploys Java and WebApp sources to Tomcat, fast"
	echo "The slow way is: run 'mvn package' to build the WAR,"
	echo "and then (re-)deploy that onto Tomcat."
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
			echoerr "Unknown flag: '$arg'"
			exit 1
			;;
	esac
done

# Deploys compiled Java classes
# (you need to run mvn compile manually, first)
sudo rsync --quiet --archive \
	target/classes/es/ \
	"$webapp_oops_root/WEB-INF/classes/es/"

# Deploys webapp sources
sudo cp \
	src/main/webapp/*.{jsp,html} \
	"$webapp_oops_root/"
