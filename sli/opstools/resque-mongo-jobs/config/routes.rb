CookieMonster::Application.routes.draw do

  match 'eat/:food' => 'eat#food'

end
