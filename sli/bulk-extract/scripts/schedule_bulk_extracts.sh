#!/bin/bash

# Bulk Extract scheduling script.
# This script is only to be run upon installation of bulk extractor, or a change in bulk extract scheduling.
# The script removes all previous bulk extract entries in the user's cron tab file, and replaces them
# with the entries in the bulk extract scheduling configuration file.
# This script must be run as the same user for bulk extract, notably root.

is_in_list() {
    # Check if string is included in list.
    local search="$1"
    shift
    local list=("$@")
    for word in "${list[@]}" ; do
        [[ $word == $search ]] && return 0
    done
    return 1
}

is_non_comment_non_blank_line() {
    # Check for non-comment/non-blank file line.
    local line="$1"
    NCNB_LINE=`echo "${line[@]}" | grep '^[ \t]*[^#]'`
    if [ -n "${NCNB_LINE}" ]
    then
      return 0
    fi
    return 1
}

months=( "jan" "feb" "mar" "apr" "may" "jun" "jul" "aug" "sep" "oct" "nov" "dec" )
days=( "sun" "mon" "tue" "wed" "thu" "fri" "sat" )

is_in_range() {
  # Check if integral value is in range.
  local val=$1
  local min=$2
  local max=$3
  ([ $min -le $val ] && [ $val -le $max ]) && return 0
  return 1
}

is_valid_line() {
  # Check for valid config file line.
  RETURN=0
  local line_number="$1"
  shift

  # Check for valid minute value.
  local minute="$1"
  if ([ -n "${minute##*[!0-9]*}" ] && ( ! is_in_range $minute 0 59 ))
  then
    echo "Config file line ${line_number}: Minute value ${minute} is out of range (valid range is 0-59)."
    RETURN=1
  elif [ -z "${minute##*[!0-9\*\-\,]*}" ]
  then
    echo "Config file line ${line_number}: Minute value ${minute} has invalid character(s) (valid characters are 0-9, *, - or ,)."
    RETURN=1
  fi

  # Check for valid hour value.
  local hour="$2"
  if ([ -n "${hour##*[!0-9]*}" ] && ( ! is_in_range $hour 0 23 ))
  then
    echo "Config file line ${line_number}: Hour value ${hour} is out of range (valid range is 0-23)."
    RETURN=1
  elif [ -z "${hour##*[!0-9\*\-\,]*}" ]
  then
    echo "Config file line ${line_number}: Hour value ${hour} has invalid character(s) (valid characters are 0-9, *, - or ,)."
    RETURN=1
  fi

  # Check for valid day-of-month value.
  local day_of_month="$3"
  if ([ -n "${day_of_month##*[!0-9]*}" ] && ( ! is_in_range $day_of_month 1 31 ))
  then
    echo "Config file line ${line_number}:  Day-of-month value ${day_of_month} is out of range (valid range is 1-31)."
    RETURN=1
  elif [ -z "${day_of_month##*[!0-9\*\-\,]*}" ]
  then
    echo "Config file line ${line_number}: Day-of-month value ${day_of_month} has invalid character(s) (valid characters are 0-9, *, - or ,)."
    RETURN=1
  fi

  # Check for valid month value.
  local month="$4"
  if ([ -n "${month##*[!0-9]*}" ] && ( ! is_in_range $month 1 12 ))
  then
    echo "Config file line ${line_number}: Month value ${month} is out of range (valid range is 1-12)."
    RETURN=1
  elif [ -z "${month##*[!0-9\*\-\,]*}" ] && ( ! is_in_list $month "${months[@]}" )
  then
    echo "Config file line ${line_number}: Month value ${month} has invalid character(s) (valid characters are 0-9, *, -, , or jan-dec)."
    RETURN=1
  fi

  # Check for valid day-of-week value.
  local day_of_week="$5"
  if ([ -n "${day_of_week##*[!0-9]*}" ] && ( ! is_in_range $day_of_week 0 7 ))
  then
    echo "Config file line ${line_number}: Day-of-week value ${day_of_week} is out of range (valid range is 0-7)."
    RETURN=1
  elif [ -z "${day_of_week##*[!0-9\*\-\,]*}" ] && ( ! is_in_list $day_of_week "${days[@]}" )
  then
    echo "Config file line ${line_number}: Day-of-week value ${day_of_week} has invalid character(s) (valid characters are 0-9, *, -, , or sun-sat)."
    RETURN=1
  fi

  return ${RETURN}
}

is_absolute_path() {
  local pathname="$1"
  [[ "${pathname}" = /* ]] && return 0 || return 1
}

process_config_file() {
  # Verify parameters.
  SCHEDULING_SCRIPT=`basename $0`
  if [ $# -lt 1 ] || [ $# -gt 2 ]
  then
    echo "Usage: ${SCHEDULING_SCRIPT} bulk_extract_script_dir [ config_file_dir ]"
    exit 1
  fi

  # Make sure bulk extract script directory is absolute and exists.
  BULK_EXTRACT_SCRIPT_DIR="$1"
  if (! is_absolute_path "${BULK_EXTRACT_SCRIPT_DIR}" )
  then
    echo "Error: Bulk extract script directory ${BULK_EXTRACT_SCRIPT_DIR} is not an absolute path."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi
  if [ ! -d ${BULK_EXTRACT_SCRIPT_DIR} ]
  then
    echo "Error: Bulk extract script directory ${BULK_EXTRACT_SCRIPT_DIR} does not exist."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi

  # Make sure bulk extract script exists, is readable, and non-zero length.
  BULK_EXTRACT_SCRIPT="${BULK_EXTRACT_SCRIPT_DIR}/local_bulk_extract.sh"
  if [ ! -f ${BULK_EXTRACT_SCRIPT} ]
  then
    echo "Error: Bulk extract script ${BULK_EXTRACT_SCRIPT} does not exist."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi
  if [ ! -s ${BULK_EXTRACT_SCRIPT} ]
  then
    echo "Error: Bulk extract script ${BULK_EXTRACT_SCRIPT} is zero-length."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi
  if [ ! -r ${BULK_EXTRACT_SCRIPT} ]
  then
    echo "Error: Bulk extract script ${BULK_EXTRACT_SCRIPT} is not readable."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi
  if [ ! -x ${BULK_EXTRACT_SCRIPT} ]
  then
    echo "Error: Bulk extract script ${BULK_EXTRACT_SCRIPT} is not executable."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi

  # Make sure config file directory exists.
  CONFIG_FILE_DIR=`dirname "$0"`
  if [ $# -eq 2 ]
  then
    CONFIG_FILE_DIR="$2"
  fi
  if [ ! -d ${CONFIG_FILE_DIR} ]
  then
    echo "Error: Config file directory ${CONFIG_FILE_DIR} does not exist."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi

  # Make sure config file exists, is readable, and non-zero length.
  SCHEDULER_CONFIGFILE="${CONFIG_FILE_DIR}/bulk_extract_scheduling.conf"
  if [ ! -f ${SCHEDULER_CONFIGFILE} ]
  then
    echo "Error: Config file ${SCHEDULER_CONFIGFILE} does not exist."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi
  if [ ! -s ${SCHEDULER_CONFIGFILE} ]
  then
    echo "Error: Config file ${SCHEDULER_CONFIGFILE} is zero-length."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi
  if [ ! -r ${SCHEDULER_CONFIGFILE} ]
  then
    echo "Error: Config file ${SCHEDULER_CONFIGFILE} is not readable."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi

  # Ensure the user really wants to do this!
  USER=`whoami`
  read -p "You are about to update the crontab for user ${USER}.  Do you want to proceed (y/n)? " RESP
  if [ "$RESP" != "y" ]
  then
    echo "Exiting.  No crontab changes will be made."
    exit 0
  fi

  # Read config file line by line.
  echo "Reading config file ${SCHEDULER_CONFIGFILE}."
  NEW_BE_CRONTAB_FILE="/tmp/bulk_extract_crontab"
  CONF_FILE_IS_VALID="true"
  LINE_NO=0
  while read -a LINE
  do
    (( LINE_NO++ ))
    if ( is_non_comment_non_blank_line "${LINE[@]}" )
    then
      if ( is_valid_line ${LINE_NO} "${LINE[@]}" )
      then
        CRON_SCHED=`echo "${LINE[@]}" | awk '{print $1, $2, $3, $4, $5}'`
        TENANT_ID="${LINE[5]}"
        CRON_LINE="${CRON_SCHED} ${BULK_EXTRACT_SCRIPT} -t${TENANT_ID}"
        echo "${CRON_LINE}" >> ${NEW_BE_CRONTAB_FILE}
      else
        CONF_FILE_IS_VALID="false"
      fi
    fi
  done < ${SCHEDULER_CONFIGFILE}
  if [ "${CONF_FILE_IS_VALID}" != "true" ]
  then
    echo "Error: Config file ${SCHEDULER_CONFIGFILE} contains invalid entries."
    echo "Exiting.  No crontab changes will be made."
    rm -f ${NEW_BE_CRONTAB_FILE}
    exit 1
  fi

  # Write new bulk extract crontab entries.
  if [ -s ${NEW_BE_CRONTAB_FILE} ]
  then
    # Delete old bulk extract crontab entries.
    TEMP_CRONTAB_FILE="/tmp/temp_crontab"
    crontab -l | grep -v ${BULK_EXTRACT_SCRIPT} | grep -v "# Bulk Extract scheduling" > ${TEMP_CRONTAB_FILE}

    # Add new new bulk extract crontab entries.
    echo "# Bulk Extract scheduling" >> ${TEMP_CRONTAB_FILE}
    cat ${NEW_BE_CRONTAB_FILE} >> ${TEMP_CRONTAB_FILE}

    # Reset crontab file.
    if ( crontab ${TEMP_CRONTAB_FILE} )
    then
      echo "Installed new crontab for user ${USER}."
      rm -f ${NEW_BE_CRONTAB_FILE} ${TEMP_CRONTAB_FILE}
    else
      echo "Error: Config file ${SCHEDULER_CONFIGFILE} contains invalid entries."
      echo "Exiting.  No crontab changes will be made."
      rm -f ${NEW_BE_CRONTAB_FILE} ${TEMP_CRONTAB_FILE}
      exit 1
    fi
  else
    echo "Exiting.  No crontab changes will be made."
  fi
  exit 0
}

#############
# MAIN
#############
process_config_file "$@"
