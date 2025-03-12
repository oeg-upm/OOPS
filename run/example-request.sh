#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2025 Pieter Hijma <info@pieterhijma.net>
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
#script_dir="$(dirname "$script_path")"
script_name="$(basename "$script_path")"

MIME_JSON="application/json"
MIME_TEXT="text/plain"
MIME_XML="application/xml"

# parameters
api_endpoint="http://localhost:8080/oops-2.0.0-SNAPSHOT/rest"
request_content_mime_type="application/xml"
request_content_file="src/test/resources/example-request-content.xml"
response_mime_type="$MIME_XML"
response_mime_type="$MIME_TEXT"

function print_help() {

	echo "Runs a sample REST request"
	echo " on a locally running instance of OOPS!"
	echo
	echo "Usage:"
	echo "  $script_name [OPTION...]"
	echo "Options:"
	echo "  -j, --json"
	echo "    Sets the content type to be requested to \"$MIME_JSON\""
	echo "  -t, --text"
	echo "    Sets the content type to be requested to \"$MIME_TEXT\""
	echo "  -x, --xml"
	echo "    Sets the content type to be requested to \"$MIME_XML\""
	echo "  -r, --request-type <MIME_TYPE>"
	echo "    Sets the content type to be requested"
	echo "    via the \"Accept\" HTTP header."
	echo "    Supported values:"
	echo "    - \"$MIME_JSON\""
	echo "    - \"$MIME_TEXT\""
	echo "    - \"$MIME_XML\""
	echo "    (default: \"$request_content_mime_type\")"
	echo "  -h, --help"
	echo "    Print this usage help and exits"
}

# Process command line arguments
while [[ $# -gt 0 ]]
do
	arg="$1"
	shift # $2 -> $1, $3 -> $2, ...

	case "$arg" in
		-j|--json)
			response_mime_type="$MIME_JSON"
			break;
			;;
		-t|--text)
			response_mime_type="$MIME_TEXT"
			break;
			;;
		-x|--xml)
			response_mime_type="$MIME_XML"
			break;
			;;
		-r|--request-type)
			response_mime_type="$1"
			shift
			break;
			;;
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

>&2 echo "INFO Sending \"$request_content_mime_type\""
>&2 echo "INFO Requesting \"$response_mime_type\""
>&2 echo "INFO Running ..."

curl -X POST "$api_endpoint" \
     -H "Content-Type: $request_content_mime_type" \
     -H "Accept: $response_mime_type" \
     -d @"$request_content_file"

>&2 echo -e "\nINFO done."

