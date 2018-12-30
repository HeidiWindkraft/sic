#!/usr/bin/perl

use strict;
use warnings;

my $USAGE = <<EOS;
USAGE: UpdateLinkedResources.pl path/to/eclipse_projects
EOS
;

die $USAGE unless defined $ARGV[0];

my $projPath = $ARGV[0];
$projPath = $projPath."/";
while ($projPath =~ s/\/\//\//g) {}
my $linkedResDescPath = $projPath."LinkedResources";

my $lrfh = undef;
open ($lrfh, '<', $linkedResDescPath) or die "Cannot read $linkedResDescPath: $!\n";
my @lrlines = <$lrfh>;
close $lrfh;

my $lrxml = "<linkedResources>\n";
for my $line (@lrlines) {
	next if ($line =~ m/^\s*$/);
	next if ($line =~ m/^#/);
	if ($line =~ m/^([^:\s]+)\s*:\s*([^#\s]+)/) {
		my $name = $1;
		my $location = $2;
		$name = $name."/";
		while ($name =~ s/\/\//\//g) {}
		$location = $location."/";
		while ($location =~ s/\/\//\//g) {}
		my $locPath = $projPath.$location;
		if (-e $locPath) {
			my $parentDir = "";
			if ($location =~ m/^((\.\.\/)+)/) {
				$parentDir = $1;
			}
			my $parentLevel = int((length $parentDir) / 3);
			my $filePrefix = $projPath.$parentDir;
			my $parentPrefix = "PARENT-$parentLevel-PROJECT_LOC/";
			print STDERR "LOG: name        : $name\n";
			print STDERR "LOG: loaction    : $location\n";
			print STDERR "LOG: locPath     : $locPath\n";
			print STDERR "LOG: parentDir   : $parentDir\n";
			print STDERR "LOG: parentLevel : $parentLevel\n";
			print STDERR "LOG: filePrefix  : $filePrefix\n";
			print STDERR "LOG: parentPrefix: $parentPrefix\n";
			print STDERR "\n";
			my @jfiles = `find $locPath -name "*.java"`;
			for my $jfile (@jfiles) {
				if ($jfile =~ m/^$filePrefix(.*)$/) {
					my $locUriTail = $1;
					if ($jfile =~ m/^$locPath(.*)$/) {
						my $nameTail = $1;
						$lrxml = $lrxml.<<EOS;
		<link>
			<name>$name$nameTail</name>
			<type>1</type>
			<locationURI>$parentPrefix$locUriTail</locationURI>
		</link>
EOS
;
					} else {
						print STDERR "WARN: Ignored file because it seems to be in wrong location (2): $jfile";
					}
				} else {
					print STDERR "WARN: Ignored file because it seems to be in wrong location: $jfile";
				}
			}
		} else {
			print STDERR "WARN: Doesn't exist: $locPath\n"
		}
	} else {
		die "Couldn't parse line: $line";
	}
}
$lrxml = $lrxml."\t</linkedResources>";


# replace the linked resources in .project

my $projDescPath = $projPath.".project";
my $projContent = "";

{
	my $projFh = undef;
	local $/ = undef;
	open ($projFh, '<', $projDescPath) or die "Cannot read $projDescPath: $!";
	binmode $projFh;
	$projContent = <$projFh>;
	close ($projFh);
}

$projContent =~ s/\<linkedResources\>.*\<\/linkedResources\>/$lrxml/sm
	or die "Failed to find <linkedResources>...</linkedResources> section in $projDescPath";

{
	my $projFh = undef;
	open ($projFh, '>', $projDescPath) or die "Cannot write $projDescPath: $!";
	print $projFh $projContent;
	close ($projFh);
}




