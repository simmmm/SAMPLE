#!/usr/bin/perl

my $var = "SimonTest";

printf $var . "\n";

sub Sub
{
	printf "Sub()\n";
	return unless defined wantarray;

	my @array = (1, 2, 3, 4, 5);

	return wantarray ? @array : "abcd";
}

Sub();

my $a = Sub();
printf $a . "\n";

my @b = Sub();

foreach my $c (@b)
{
	printf $c."\n";
}
