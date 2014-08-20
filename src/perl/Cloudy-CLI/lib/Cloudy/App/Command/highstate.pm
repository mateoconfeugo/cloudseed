package Cloudy::App::Command::highstate;
########################################################################
# ABSTRACT:  command line app to mixup a recipe provided the recipe id
########################################################################
use Moose;
use Moose::Util qw(apply_all_roles);
use Devel::REPL;
use File::Spec::Functions qw(catfile catdir);
use Cloudy::Client;

extends qw(MooseX::App::Cmd::Command);

#with 'MooseX::Log::Log4perl';

has usage => (isa => 'Int', is  => 'rw', defaults=>'send a command for the master to execute');
has app => (is  => 'rw', lazy_build=>1);

sub BUILD {
    my $self = shift;
    my $path = catfile($ENV{BAILOUT_ROOT_DIR}, 'log4perl.conf');
#    Log::Log4perl::init_and_watch($path, 60);
 #   $self->logger(Log::Log4perl->get_logger());
    return $self;
}

sub _build_app {
    my $self = shift;
    return Cloudy::Client->new();
}

sub execute {
  my ( $self, $opt, $args ) = @_;
  # probably use AnyEvent to handle the return
  my $result = $self->app->highstate($args);
  return $result;
}

sub run  {
  my $cmd = __PACKAGE__->new({env=>'dev', 'target-spec'=>'box1'});
  $cmd->execute();
}

run() unless caller;

no Moose;
1;
