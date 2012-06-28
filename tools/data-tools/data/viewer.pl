#!/usr/bin/perl
use strict;
use XML::Simple;
use Data::Dumper;
use File::Find qw(find);

sub handleXml;
sub recurse;
sub genCode;
sub matches;
sub execute;
my $nodeList = [];
print "Initializing data.....\n";
find sub {
    my $filename = $_;
    if ($filename =~ /.*xml/) {
        &handleXml($filename);
    }
},  
".";

sub handleXml {
    my ($filename) = @_;
    my $fullPath = $File::Find::name;
    print "Processing File [$filename]\n";
    my $simple = XML::Simple->new();
    my $data   = $simple->XMLin($filename, KeyAttr=>[], ForceArray=>[]);
    foreach my $top (keys(%{$data})) {
        if($top !~ /xmlns/) {
            my @stack = ();
            push @stack, $top;
            &recurse($data->{$top}, \@stack, $fullPath);
        }
    }
}

sub recurse {
	my ($tree, $stack, $filename) = @_;
	if(UNIVERSAL::isa( $tree, "HASH" )) {
		foreach my $node (keys(%{$tree})) {
			my $val = $tree->{$node};
				push(@{$stack}, $node);
				&recurse($val, $stack, $filename);
				pop(@{$stack});
		}
	}
	elsif(UNIVERSAL::isa( $tree, "ARRAY" )) {
                my $arraySize = @$tree;
		push(@{$stack}, "[$arraySize]");
		&recurse($tree->[0], $stack, $filename);
		pop(@{$stack});
	}
	else {
		my $path = join("->", @{$stack});
                my @stackCopy = map  {$_} @{$stack};
                push (@{$nodeList}, ["$filename",\@stackCopy]);
                my $path = join("->", @stackCopy);
	}
}

foreach my $node (@{$nodeList}) {
   my $file  = $node->[0];
   my $stack = $node->[1];
   my @modStack = map {if(/.*\[.*/) {$_}else{ "{'$_'}"} } @{$stack}; 
   my $path = join("->", @modStack);
   $node->[1] = $path;
}

sub genCode {
	my ($pr, $code, $depth) = @_;
        my @p = @{$pr};
	my $vari = shift @p;
	if(!defined($vari)) {
            return " print \$data->$code".',"\n";';
	}
	else {
                my $token = "x$depth";
                $code =~ s/\[\d+\]/[\$$token]/;
		return "for (my \$$token = 0; \$$token < $vari; \$$token++){\n".genCode(\@p, $code, ++$depth)."\n}";
	}
}

sub repl {
    my $reconsider = undef;
    while(1) {
        my $q = undef;
        if($reconsider) {
            $q = $reconsider;
        }
        else {
            print ">>";
            $q = <STDIN>;
            $reconsider = undef;
        }
        chomp($q);
        my @matchResults = &matches($q); 
        my $mCount = @matchResults;
        if($mCount == 0) {
            #do nothing
        }
        elsif($mCount == 1) {
            #print "Matched exactly one! Executing!\n";
            &execute($matchResults[0]);
        }
        else {
            my $i = 0;
            foreach my $s (@matchResults) {
                print  "$i -> ", $matchResults[$i++]->[1], "\n";
            }
            while(<STDIN>){
                my $choice = $_;
                if($choice =~ /\d+/) {
                    my $chosen = $matchResults[$choice];
                    &execute($chosen);
                }
                else {
                    $reconsider=$choice;
                    last;
                }
            }
        }
    }
}

sub matches {
    my ($m) = @_;
    my @results = ();
    print "Searching for  [$m]\n";
    foreach my $node (@{$nodeList}) {
        my $path = $node->[1];
        if ($path =~ /.*$m.*/i) {
            #print "Found match $path\n";
            push(@results, $node);
        }
    }
    return @results;
}

sub execute {
	my ($fileAndCode) = @_;
	my $simple = XML::Simple->new();
	my ($filename, $code) = @{$fileAndCode};
	my $data   = $simple->XMLin("$filename", KeyAttr=>[], ForceArray=>[]);

	my @vars = ();
	while($code =~ /\[(\d+)\]/g) {
		push(@vars, $1); 
	}
        print "$code\n";
        print eval(&genCode(\@vars, $code , 0));
}

&repl();
