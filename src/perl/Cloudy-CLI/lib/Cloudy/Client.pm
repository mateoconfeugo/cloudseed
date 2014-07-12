package Cloudy::Client
# ABSTRACT:  Interact with the orcestration topology distributing the state and highstate function graphs
use Moose;
use File::Spec::Functions qw(catfile catdir);
use Net::Kestrel;
use Cloudy::Configuration;

with 'Cloudy::DB';

has config => (is=>'rw', lazy_build=>1);
has command_queue => (is=>'rw', lazy_build=>1);
has results_queue => (is=>'rw', lazy_build=>1);
has queue_manager => (is=>'rw', lazy_build=>1);

sub highstate {
    my ($self, $args) = @_;
    my $cmd = $args->{cmd};
    $self->queue_manger->put($self-command_queue, $cmd);
}

sub _build_config { return Cloudy::Configuration->new() }
sub _build_queue_manager { return Net::Kestrel->new; }

sub run {
  my $cfg = Bailout::Configuration->new();
  my $api = Cloudy::Client->new();
  my $args = {};
  $api->highstate($args);
}

run() unless caller;

__PACKAGE__->meta->make_immutable;
no Moose;
1;
