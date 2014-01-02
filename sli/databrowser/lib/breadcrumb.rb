module Breadcrumbhelper

  # represents one 'crumb' on the breadcrumb trail; contains
  # a name and a URL value.  We put these in an array in a
  # session to implement the breadcrumb trail.
  class Breadcrumb
    def initialize (name, link)
      @name = name
      @link = link
    end

    def name
      return @name
    end

    def link ; return @link; end

  end

end
