require 'mobpage/page_object/accessors'
require 'mobpage/page_object/navigation'

module PageObject
  module Accessors
    alias_method :button, :single
    alias_method :switch, :single
    alias_method :pin, :single
    alias_method :link, :single
    alias_method :text_field, :single
    alias_method :check_box, :single
    alias_method :select_list, :single
    alias_method :item_list, :single
    alias_method :element, :single
    alias_method :radio_button, :single
    alias_method :label, :single
    alias_method :image, :single
    alias_method :paragraph, :single
    alias_method :dialog, :single
    alias_method :spinner, :single
    alias_method :view_switcher, :single
    alias_method :picker, :single

    alias_method :buttons, :collection
    alias_method :switches, :collection
    alias_method :pins, :collection
    alias_method :links, :collection
    alias_method :text_fields, :collection
    alias_method :check_boxes, :collection
    alias_method :select_lists, :collection
    alias_method :item_lists, :collection
    alias_method :elements, :collection
    alias_method :radio_buttons, :collection
    alias_method :labels, :collection
    alias_method :images, :collection
    alias_method :paragraphs, :collection
    alias_method :dialogs, :collection
    alias_method :spinners, :collection
    alias_method :view_switchers, :collection
    alias_method :pickers, :collection
  end
end