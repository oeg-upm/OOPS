#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2025 Pieter Hijma <info@pieterhijma.net>
# SPDX-License-Identifier: Apache-2.0
#
# See the output of "$0 -h" for details.

# Exit immediately on each error and unset variable;
# see: https://vaneyckt.io/posts/safer_bash_scripts_with_set_euxo_pipefail/
set -Eeuo pipefail

script_path="$(readlink -f "${BASH_SOURCE[0]}")"
script_dir="$(dirname "$script_path")"
script_name="$(basename "$script_path")"

data_dir="$script_dir/../src/test/resources/data"
input_dir="$data_dir/input"
valid_pitfalls=("P02" "P03" "P04" "P05" "P06" "P07" "P08" "P10" \
	"P10-A" "P10-B" "P10-C" "P11" "P12" "P13" "P19" "P20" "P21" \
	"P22M1" "P22M2" "P22M3" "P22M4" \
	"P24" "P25" "P26" "P27" "P28" "P29" "P30" "P31" "P32" "P33" "P34" "P35" "P36" \
	"P38" "P39" "P40" "P41")

# parameters
api_endpoint="http://localhost:8080/oops-2.0.0-SNAPSHOT/rest"

function print_help() {

	echo "Runs a test via the REST service"
	echo
	echo "Usage:"
	echo "  $script_name [OPTION...] <pitfall>"
	echo "    where <pitfall> is one of ${valid_pitfalls[*]}"
	echo
	echo "Options:"
	echo "  -h, --help"
	echo "    Print this usage help and exits"
}

# Parse options manually
while [[ $# -gt 0 ]]; do
    case "$1" in
        -h|--help)
            print_help
	    exit 0
            ;;
        --) # Stop parsing options
            shift
            break
            ;;
        -*)
            echo "Error: Unknown option '$1'"
            print_help
	    exit 1
            ;;
        *)
            break
            ;;
    esac
done

# Ensure exactly one positional argument (pitfall)
if [[ $# -ne 1 ]]; then
    echo "Error: Missing or too many <pitfall> arguments."
    print_help
    exit 1
fi

pitfall="$1"

# Validate pitfall
if [[ ! " ${valid_pitfalls[*]} " =~ " ${pitfall} " ]]; then
    echo "Error: Invalid pitfall '${pitfall}'. Must be one of ${valid_pitfalls[*]}."
    print_help
    exit 1
fi

# echo "Selected pitfall: $pitfall"

prefix_xml_request="<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<OOPSRequest>
  <OntologyUrl></OntologyUrl>
  <OntologyContent><![CDATA[
"
suffix_xml_request=" ]]></OntologyContent>
</OOPSRequest>"

{ printf "%s" "$prefix_xml_request"; cat "$input_dir/${pitfall}.owl"; printf "%s" "$suffix_xml_request"; } \
    | curl -X POST "$api_endpoint" -H "Content-Type: application/xml" -H "Accept: text/turtle" -d @-

