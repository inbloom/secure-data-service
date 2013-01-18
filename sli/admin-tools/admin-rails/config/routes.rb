=begin

Copyright 2012 Shared Learning Collaborative, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


SLIAdmin::Application.routes.draw do

  resources :forgot_passwords do 
    post 'reset', :on => :collection
  end

  resources :change_passwords
  resources :waitlist_users
  resources :admin_delegations

  resources :realm_management
  post "landing_zone/provision", :to => 'landing_zone#provision'
  get "landing_zone/provision", :to => 'landing_zone#provision'
  get "landing_zone", :to => 'landing_zone#index'


  resources :account_managements
  resources :application_authorizations

  get "sessions/new"

  resources :sessions
  resources :apps
  resources :custom_roles
  resources :home
  match '/apps/approve', :to => 'apps#approve'
  match '/apps/unregister', :to => 'apps#unregister'

  get 'apps/sea', :to => 'apps#get_state_edorgs'
  get 'lea', :to => 'apps#get_local_edorgs'
  get 'change_passwords', :to => 'change_passwords#new'
  get 'forgot_passwords', :to => 'forgot_passwords#reset'

  match '/logout', :to => 'sessions#destroy'
  match '/callback', :to => 'application#callback'

  resources :user_account_registrations
  resources :user_account_validation
  resources :user_account_registration

  match "/eula" => "eulas#show", :via => :get
  match "/eula" => "eulas#create", :via => :post 
  match "/registration" => "user_account_registrations#new", :via => :get
  match "/changePassword" => "change_passwords#new", :via => :get
  match "/forgotPassword" => "forgot_passwords#index", :via => :get
  match "/forgot_passwords" => "forgot_passwords#index", :via => :get
  match "/forgotPassword/notify" => "forgot_passwords#show", :via => :get, :as => "forgot_password_notify"
  match "/forgotPassword/success" => "forgot_passwords#success", :via => :get
  match "/resetPassword" => "forgot_passwords#update", :via => :get
  match "/resetPassword/new" => "forgot_passwords#new", :via => :get

  # matches the model in NewAccountPassword
  match "/resetPassword/newAccount/:key" => "new_accounts#index", :via => :get, :as => "new_account_passwords"
  match "/resetPassword/newAccount/:key" => "new_accounts#set_password", :via => :post, :as => "new_account_passwords"

   # matches the new samt account notification email
  match "/reset_password" => "forgot_passwords#index", :via => :get
  
  root :to => 'home#index'

  resources :users , :constraints => { :id => /[^\/]+/ }
  
end
