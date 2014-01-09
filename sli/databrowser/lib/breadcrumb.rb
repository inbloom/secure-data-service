module Breadcrumbhelper

  # represents one 'crumb' on the breadcrumb trail; contains
  # a name and a URL value.  We put these in an array in a
  # session to implement the breadcrumb trail.
  class Breadcrumb
    def initialize (name, strippedLink, actualLink)
      @name         = name
      @strippedLink = strippedLink
      @actualLink   = actualLink
    end

    def name;         return @name;         end
    def strippedLink; return @strippedLink; end
    def actualLink;   return @actualLink;   end

  end

end
