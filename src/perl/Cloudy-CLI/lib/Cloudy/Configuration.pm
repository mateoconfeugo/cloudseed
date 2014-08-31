package Cloudy::Configuration;
# ABSTRACT:  Interact with the orcestration topology distributing the state and highstate function graphs
use Moose;
use File::Spec::Functions qw(catfile catdir);

has settings => (is=>'rw', lazy_build=>1);

sub _build_settings { return Cloudy::Configuration->new() }

sub run {
  my $cfg = Bailout::Configuration->new();
}

run() unless caller;

__PACKAGE__->meta->make_immutable;
no Moose;
1;
