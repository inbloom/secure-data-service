#StudentCohort :31920
#StudentSchool :31696
#StudentSection :31696
#StudentAssessment :63392
#StudentParent :48048


my @files = ('1.counts.txt', '2.counts.txt', '3.counts.txt');

my $stats = {};
foreach my $file (@files) {
    print "$file\n";
    open FILE, "<$file" or die "Cannot open file:$file";
    while(<FILE>){
        if (/(\S+) :(\d+)/) {
	    my($coll, $count) = ($1, $2);
	    #print "$file $coll $count\n";
	    $stats->{$coll}->{$file}= $count;
	}
    }
}

my @colls = sort keys %{$stats};

foreach my $coll (@colls) {
        my $c1 = $stats->{$coll}->{$files[0]};
        my $c2 = $stats->{$coll}->{$files[1]};
        my $c3 = $stats->{$coll}->{$files[2]};

	my ($ratio1, $ratio2) = (0, 0);
	$ratio1 = ($c2 * 1.0)/$c1 if ($c1);
	$ratio2 = ($c3 * 1.0)/$c1 if ($c1);
	print sprintf "%-40s,%-10f,%-10f,%-10f,%-10f,%-10f\n", $coll, $c1, $c2, $ratio1, $c3, $ratio2;
}


