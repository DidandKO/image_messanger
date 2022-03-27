window.onload = unit;
function unit() {
    document.getElementById('change_photo_form').style.display = 'none';

    let button_add_chat = document.getElementById('add_chat_id');
    let field_creating_chat = document.getElementById('creating_chat');
    let nav_menu = document.getElementById('nav_menu_id');
    let burger_logo = document.getElementById('burger_logo_id');
    let container_creating_chat = document.getElementById('container_creating_chat_id');
    let open_settings = document.getElementById('open_settings_ico');
    let open_aboutus = document.getElementById('open_about_ico');
    let open_logout = document.getElementById('open_logout_ico');
    let settings = document.getElementById('settings-container-id');
    let back_settings = document.getElementById('back_id');
    let change_img_btn = document.getElementById('change_photo');
    let chatroom = document.getElementById('chat_room_id');
    let filter = document.getElementById('filter');

    if ($('.chats').length >= 7){
        chatroom.classList.add('scroll');
    }
    chatroom.addEventListener('mouseover', function () {
        button_add_chat.classList.toggle('add_chat-show');
    });
    chatroom.addEventListener('mouseout', function () {
        button_add_chat.classList.remove('add_chat-show');
    });
    button_add_chat.addEventListener('click', function () {
        field_creating_chat.style.display = 'block';
        field_creating_chat.classList.add('show-set');
        field_creating_chat.classList.remove('hide-set');

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

            field_creating_chat.classList.remove('show-set');
            setTimeout(() => field_creating_chat.style.display = 'none', 150);
            field_creating_chat.classList.remove('show-set');
            field_creating_chat.classList.add('hide-set');

        }
        if ((!withinNavMenu)&&(!withinBurger)&&(!withinContainerSettings)){

            nav_menu.classList.add("nav-hide");
            nav_menu.classList.remove("nav-show");
            burger_logo.classList.remove('burger_logo_active');
            filter.style.display ='none';
        }

        if ((!withinContainerSettings) && (!withinSettings) && (settings.style.display === 'block') || (backSettings)){
            filter.style.display = 'none';
            if(document.getElementById('message-field-id') != null) {
                document.getElementById('message-field-id').style.background = '#FFF';
            }
            setTimeout(() => settings.style.display = 'none', 150);
            settings.classList.remove('show-set');
            settings.classList.add('hide-set');
        }
        if(backSettings){
            filter.style.display = 'block';
            filter.classList.remove('filter-blur');
            filter.classList.add('filter-shadow');
        }
    });

    burger_logo.addEventListener('click', function (){
        filter.style.display ='block';
        nav_menu.classList.toggle("nav-hide");
        nav_menu.classList.toggle("nav-show");
        burger_logo.classList.toggle('burger_logo_active');
        filter.classList.remove('filter-blur');
        filter.classList.add('filter-shadow');
    });

    open_settings.addEventListener('click', function(){
        settings.style.display = 'block';
        filter.style.display ='block';
        if(document.getElementById('message-field-id') != null) {
            document.getElementById('message-field-id').style.background = '#DED1D1';
        }
        settings.classList.add('show-set');
        settings.classList.remove('hide-set');
        filter.classList.remove('filter-shadow');
        filter.classList.add('filter-blur');
    });

    change_img_btn.addEventListener('click', function(){
        document.getElementById('change_photo_form').style.display = 'block';
    });
}