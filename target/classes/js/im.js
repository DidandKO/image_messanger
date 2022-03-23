window.onload = unit;
function unit() {
    document.getElementById('change_photo_form').style.display = 'none';

    let button_add_chat = document.getElementById('add_chat_id');
    let field_creating_chat = document.getElementById('creating_chat');
    let nav_menu = document.getElementById('nav_menu_id');
    let burger_logo = document.getElementById('burger_logo_id');
    let burger_logo_user = document.getElementById('burger_logo_user');
    let container_creating_chat = document.getElementById('container_creating_chat_id');
    let open_settings = document.getElementById('open_settings_ico');
    let open_aboutus = document.getElementById('open_about_ico');
    let open_logout = document.getElementById('open_logout_ico');
    let settings = document.getElementById('settings-container-id');
    let back_settings = document.getElementById('back_id');
    let change_img_btn = document.getElementById('change_photo');
    // let show_text_btns = document.getElementsByClassName('trans-but');
    //
    // for (let i in show_text_btns) {
    //     i.addEventListener('click', function () {
    //         let show_text_div = document.getElementById(i.id + 'a')
    //         show_text_div.setAttribute('text', i.id);
    //     });
    // }

    button_add_chat.addEventListener('click', function () {
        field_creating_chat.style.display = 'block';
        container_creating_chat.style.display = 'flex';
    });
    let button_creating_chat = document.getElementById("creating_chat_id");

    button_creating_chat.addEventListener("click", function () {
        field_creating_chat.style.display = 'none';
        document.getElementById('title_chat_id').innerText = document.getElementById('text_id').value;
    });

    document.addEventListener('click', (e) => {
        const withinCreatingChat = e.composedPath().includes(container_creating_chat);
        const withinButton = e.composedPath().includes(button_add_chat);
        const withinNavMenu = e.composedPath().includes(nav_menu);
        const withinBurger = e.composedPath().includes(burger_logo);
        const withinSettings = e.composedPath().includes(open_settings);
        const withinContainerSettings = e.composedPath().includes(settings);
        const backSettings = e.composedPath().includes(back_settings);

        if ((!withinCreatingChat) && (!withinButton)) {
            field_creating_chat.style.display = 'none';
        }
        if ((!withinNavMenu) && (!withinBurger)){
            nav_menu.style.display = 'none';
        }
        if ((!withinContainerSettings) && (!withinSettings) && (settings.style.display === 'block') || (backSettings)){
            document.getElementById('filter').style.display = 'none';
            if(document.getElementById('message-field-id') != null) {
                document.getElementById('message-field-id').style.background = '#FFF';
            }
            settings.classList.add('hide-set');
            settings.classList.remove('show-set');
            setTimeout(() => settings.style.display = 'none', 150);
        }
    });

    burger_logo.addEventListener('click', function (){
        nav_menu.style.display = 'block';
    });

    burger_logo_user.addEventListener('click', function (){
        nav_menu.style.display = 'none';
    });

    open_settings.addEventListener('click', function(){
        settings.style.display = 'block';
        nav_menu.style.display = 'none';
        document.getElementById('filter').style.display ='block';
        if(document.getElementById('message-field-id') != null) {
            document.getElementById('message-field-id').style.background = '#DED1D1';
        }
        settings.classList.add('show-set');
        settings.classList.remove('hide-set');
    });

    change_img_btn.addEventListener('click', function(){
        document.getElementById('change_photo_form').style.display = 'block';
    });
}