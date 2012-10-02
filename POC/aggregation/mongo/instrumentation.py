from timeit import default_timer

class instrument(object):
    def __init__(self):
        self.lap_stack   = []
        self.guard_stack = []
        self.lap_table   = {}

    def start(self):
        self.start_time = default_timer()

    def stop(self):
        self.end_time = default_timer()

    def lap(self, func=None, id=None):
        """ Can be used as a decorator to time a function or in the 
            'with' statement as a guard  """
        if func:
            if id is None:
                id = "%s.%s" % (func.__module__, func.__name__)
            def wrapper(*arg, **kw):
                self._start_lap(id)
                result = func(*arg, **kw)
                self._stop_lap(id)
                return result
            return wrapper
        else:
            if id is None: raise "ID must not be None !"
            self.guard_stack.append(id)
            return self

    def __enter__(self):
        self._start_lap(self.guard_stack[-1])
        return self 

    def __exit__(self, *args):
        self._stop_lap(self.guard_stack[-1])
        self.guard_stack.pop()
        return False 

    def _start_lap(self, lap_id):
        self.lap_stack.append((default_timer(), lap_id))

    def _stop_lap(self, lap_id):
        end_time = default_timer() 
        start_time, start_lap_id = self.lap_stack.pop()
        if lap_id != start_lap_id:
            raise KeyError("Invalid lap_id: %s" % start_lap_id)
        self.lap_table.setdefault(lap_id, []).append(end_time - start_time)

    def print_results(self):
        def calc_stats(lap_entries):
            t_spent = sum(lap_entries, 0.0)
            percent = (t_spent/total_time) * 100
            count = len(lap_entries)
            average = t_spent / count
            return (t_spent, percent, count, average)

        total_time = (self.end_time - self.start_time)
        print "Total time: ", total_time
        print "Laps..."
        lap_stats = [(k, calc_stats(v)) for k,v in self.lap_table.iteritems()]
        lap_stats.sort(lambda a,b: cmp(a[1][0], b[1][0]))
        for k, v in lap_stats:
            t_spent, percent, count, average = v
            print "%30.30s: %7.3f (%5.2f), %5.2d, %7.3f" % (k, t_spent, percent, count, average)


if __name__=="__main__":
    import time 
    inst = instrument() 

    @inst.lap    
    def waste_one_sec():
        time.sleep(1)

    inst.start()
    time.sleep(1)
    waste_one_sec() 
    time.sleep(1)
    for i in range(3):
        waste_one_sec()
    time.sleep(1)
    for i in range(3):
        with inst.lap(id="my_code_block"):
            time.sleep(1)
    inst.stop()
    inst.print_results() 
