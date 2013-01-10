import csv
import cStringIO
import optparse, os, random, string, sys
import logging

logging.basicConfig(level=logging.INFO)
log = logging.getLogger(__name__)



MULTIPLIERS = {
    'student'     : 1
  , 'parent'      : 2
  , 'school'      : 0.002
  , 'stu_sch'     : 1
  , 'staff '      : 0.1
  , 'stf_sch'     : 0.1
  , 'section'     : 0.4
  , 'stu_sec'     : 10
  , 'stf_sec'     : 0.4
  , 'attendance'  : 1000
  , 'assmt'       : 110
  , 'assmtdetail' : 3850
}


def random_string(length):
    """
    """
    return ''.join(random.choice(string.ascii_uppercase + \
                                 string.ascii_lowercase + \
                                 string.digits) for x in range(length))


def main():
    """
    """
    parser = optparse.OptionParser()
    
    # meta
    parser.add_option('--verbose', '-v', dest='verbose',
                      action="store_true",  
                      help="be verbose")
    parser.add_option('--doctest', '-t', dest='run_doctest', action="store_true", 
                      help="run doctests")


    options, positional_args = parser.parse_args()
    
    num_students = positional_args[0] if len(positional_args)==1 else None 

    if not num_students:
        parser.print_usage()
        return 1
    
    if options.verbose:
        log.setLevel(logging.DEBUG)
    
    if options.run_doctest:
        import doctest
        doctest.testmod()
        return 0

    left = open("left.txt", "wb")
    right = open("right.txt", "wb")
    
    c = 0
    for obj_type, m in MULTIPLIERS.items():
        qty = max(1, int(float(num_students) * float(m)))
        log.debug("type %s: %s students * multiplier %s = %s" % (obj_type, num_students, m, qty))

        for i in range(qty):
            source_id = random_string(32) 
            obj_data_crc = random_string(32)
            
            print >> right, "right", obj_type, source_id, obj_data_crc

            # randomize some variation; 
            # 2% chance that obj_data_crc will differ
            # 1% chance that obj_data_crc and source_id will differ
            r = random.random()
            if r <= 0.02:
                obj_data_crc = random_string(32)
            if r <= 0.01:
                source_id = random_string(32) 

            print >> left, "left", obj_type, source_id, obj_data_crc

            c += 1

    log.debug("total: %s" % c) 
    
    return 0



if __name__=="__main__":
    sys.exit( main() )
    
