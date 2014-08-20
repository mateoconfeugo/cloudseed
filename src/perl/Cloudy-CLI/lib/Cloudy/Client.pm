package Cloudy::Client;
# ABSTRACT:  Interact with the orcestration topology distributing the state and highstate function graphs
use Moose;
use File::Spec::Functions qw(catfile catdir);
use Net::Kestrel;
use Cloudy::Configuration;

#with 'Cloudy::DB';

has config => (is=>'rw', lazy_build=>1);
has command_queue => (is=>'rw', isa=>'Str');
has results_queue => (is=>'rw', lazy_build=>1);
has queue_manager => (is=>'rw', lazy_build=>1);

sub highstate {
    my ($self, $args) = @_;
    my $cmd = $args->{cmd};
    my $result = $self->queue_manager->put($self->command_queue, $cmd);
    return $result;
}

sub _build_config { return Cloudy::Configuration->new() }
sub _build_queue_manager { return Net::Kestrel->new; }

sub run {
  my $api = Cloudy::Client->new({command_queue=>'spam'});
  my $args = {cmd=>"salt 'somebox' state.highstate"};
  my $result = $api->highstate($args);
  return result;
}

run() unless caller;

__PACKAGE__->meta->make_immutable;
no Moose;
1;
