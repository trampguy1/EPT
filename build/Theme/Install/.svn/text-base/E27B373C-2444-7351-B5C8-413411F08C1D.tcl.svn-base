proc CreateWindow.E27B373C-2444-7351-B5C8-413411F08C1D {wizard id} {
    set base [$wizard widget get $id]

    grid rowconfigure    $base 1 -weight 1
    grid columnconfigure $base 0 -weight 1

    Label $base.image -borderwidth 0 -justify center
    grid  $base.image -row 1 -column 0
    $id widget set Image -type image -widget $base.image

    Label $base.title -height 3 -bg white -font TkCaptionFont  -autowrap 0 -justify center
    grid $base.title -row 0 -column 0 -padx 5
    $id widget set Caption -type text -widget $base.title

    Label $base.message -bg white -autowrap 1 -anchor nw -justify left
    grid  $base.message -row 2 -column 0 -sticky news -padx 5
    $id widget set Message -type text -widget $base.message

    
}

