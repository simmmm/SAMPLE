#!/usr/bin/perl

use Data::Dumper;
use XML::Writer;
use IO::File;

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

my %result = map { $_ => 1} @b;

print Dumper(\%result);

my $output = IO::File->new(">output.xml");

my $writer = XML::Writer->new(OUTPUT => $output);

$writer->startTag("ROOT");

for my $key (keys(%result))
{
	$writer->dataElement("CLEF", $result{$key}, name => $key);
}

$writer->endTag("ROOT");

$writer->end();


