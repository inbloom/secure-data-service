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
    ncnb_line=`echo "${line[@]}" | grep '^[ \t]*[^#]'`
    if [ -n "${ncnb_line}" ]
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

is_logdir_line() {
  # Check for config file bulk extract script logging directory line.
  local line="$1"
  options=`echo "${line[@]}" | grep '^[ \t]*/'`
  if [ -n "${options}" ]
  then
    return 0
  fi
  return 1
}

is_options_line() {
  # Check for config file bulk extract script options line.
  local line="$1"
  options=`echo "${line[@]}" | grep '^[ \t]*-'`
  if [ -n "${options}" ]
  then
    return 0
  fi
  return 1
}

is_cron_line() {
  # Check for config file cron job line.
  RETURN=0
  local line_number="$1"
  shift
  local line="$@"
  if ( is_options_line "${line[@]}" ) || ( is_logdir_line "${line[@]}" )
  then
    return 1
  fi

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

process_config_file() {
  # Read config file line by line.  Extract cron job entries.
  echo "Reading config file ${SCHEDULER_CONFIGFILE}."
  OPTIONS=""
  LOGDIR="${BULK_EXTRACT_SCRIPT_DIR}"/logs
  if [ -n "`mkdir -p ${LOGDIR} 2>&1`" ]
  then
    LOGDIR="/tmp/logs"
    mkdir -p ${LOGDIR}
  fi
  LOGFILE="${LOGDIR}/local_bulk_extract.log"
  NEW_BE_CRONTAB_FILE="/tmp/bulk_extract_crontab"
  CONF_FILE_IS_VALID="true"
  LINE_NO=0
  while read -a LINE
  do
    (( LINE_NO++ ))
    if ( is_non_comment_non_blank_line "${LINE[@]}" )
    then
      if ( is_cron_line ${LINE_NO} "${LINE[@]}" )
      then
        CRON_SCHED=`echo "${LINE[@]}" | awk '{print $1, $2, $3, $4, $5}'`
        TENANT_ID="${LINE[5]}"
        DELTA="${LINE[6]}"
        if [ "${DELTA}" = "delta" ]; then
            CRON_LINE="${CRON_SCHED} ${BULK_EXTRACT_SCRIPT} ${OPTIONS} -t${TENANT_ID} -d >> ${LOGFILE} 2>&1"
        else
            CRON_LINE="${CRON_SCHED} ${BULK_EXTRACT_SCRIPT} ${OPTIONS} -t${TENANT_ID} >> ${LOGFILE} 2>&1"
        fi
        echo "${CRON_LINE}" >> ${NEW_BE_CRONTAB_FILE}
      elif ( is_options_line "${LINE[@]}" )
      then
        OPTIONS="${LINE[@]}"
      elif ( is_logdir_line "${LINE[@]}" )
      then
        TMP_LOGDIR="${LINE[@]}"
        if [[ "${TMP_LOGDIR}" != /* ]]
        then
          echo "Warning: Bulk extract script logging directory ${TMP_LOGDIR} is not an absolute path."
          echo "Using default logfile ${LOGFILE} for ${BULK_EXTRACT_SCRIPT} output."
        elif [ -n "`mkdir -p ${TMP_LOGDIR} 2>&1`" ]
        then
          echo "Warning: Bulk extract script logging directory ${TMP_LOGDIR} cannot be created."
          echo "Using default logfile ${LOGFILE} for ${BULK_EXTRACT_SCRIPT} output."
        else
          LOGDIR="${TMP_LOGDIR}"
          LOGFILE="${LOGDIR}/local_bulk_extract.log"
          echo "Using logfile ${LOGFILE} for ${BULK_EXTRACT_SCRIPT} output."
        fi
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
    exit 1
  fi
  exit 0
}

check_if_absolute_path() {
  local pathdesc="$1"
  local pathname="$2"
  if [[ "${pathname}" != /* ]]
  then
    echo "Error: ${pathdesc} ${pathname} is not an absolute path."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi
}

check_if_valid_dir() {
  local dirdesc="$1"
  local dirname="$2"
  if [ ! -d ${BULK_EXTRACT_SCRIPT_DIR} ]
  then
    echo "Error: ${dirdesc} ${dirname} does not exist."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi
}

check_if_valid_file() {
  local filedesc="$1"
  local filename="$2"
  if [ ! -f ${filename} ]
  then
    echo "Error: ${filedesc} ${filename} does not exist."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi
  if [ ! -s ${filename} ]
  then
    echo "Error: ${filedesc} ${filename} is zero-length."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi
  if [ ! -r ${filename} ]
  then
    echo "Error: ${filedesc} ${filename} is not readable."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi
}

check_if_executable() {
  local filedesc="$1"
  local filename="$2"
  if [ ! -x ${filename} ]
  then
    echo "Error: ${filedesc} ${filename} is not executable."
    echo "Exiting.  No crontab changes will be made."
    exit 1
  fi
}

process_arguments() {
  # Verify parameters.
  if [ $# -lt 1 ] || [ $# -gt 2 ]
  then
    echo "Usage: ${SCHEDULING_SCRIPT} bulk_extract_script_dir [config_file_dir]"
    exit 1
  fi

  CONFIG_FILE_DIR=`dirname "$0"`
  if [ $# -eq 2 ]
  then
    CONFIG_FILE_DIR="$2"
  fi

  # Make sure bulk extract script directory is absolute and exists.
  BULK_EXTRACT_SCRIPT_DIR="$1"
  check_if_absolute_path "Bulk extract script directory" "${BULK_EXTRACT_SCRIPT_DIR}"
  check_if_valid_dir "Bulk extract script directory" "${BULK_EXTRACT_SCRIPT_DIR}"

  # Make sure bulk extract script exists, is readable, and non-zero length.
  BULK_EXTRACT_SCRIPT="${BULK_EXTRACT_SCRIPT_DIR}/local_bulk_extract.sh"
  check_if_valid_file "Bulk extract script" "${BULK_EXTRACT_SCRIPT}"
  check_if_executable "Bulk extract script" "${BULK_EXTRACT_SCRIPT}"

  # Make sure config file directory exists.
  check_if_valid_dir "Config file directory" "${CONFIG_FILE_DIR}"

  # Make sure config file exists, is readable, and non-zero length.
  SCHEDULER_CONFIGFILE="${CONFIG_FILE_DIR}/bulk_extract_scheduling.conf"
  check_if_valid_file "Config file" "${SCHEDULER_CONFIGFILE}"

  # Ensure the user really wants to do this!
  USER=`whoami`
  read -p "You are about to update the crontab for user ${USER}.  Do you want to proceed (y/n)? " RESP
  if [ "$RESP" != "y" ]
  then
    echo "Exiting.  No crontab changes will be made."
    exit 0
  fi
}

#############
# MAIN
#############
process_arguments "$@"
process_config_file "$@"
