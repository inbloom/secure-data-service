UserRegistration::Application.routes.draw do
  resources :user_account_registrations
  resources :user_account_validation
  resources :user_account_registration

  match "/eula" => "eulas#show", :via => :get
  match "/eula" => "eulas#create", :via => :post 

  root :to => 'user_account_registrations#new'
end
