use strict;
use warnings FATAL => 'all';
$| = 1;
my $counter = 0;
until ($counter > 5) {
    print $counter;
    print "\n";
    sleep(1);
    $counter++;
}
0;