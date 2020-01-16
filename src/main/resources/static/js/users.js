
$(document).ready(function () {
    console.log("ready");
    $('#userTable').DataTable({
        ajax: {
            url: "/api/user/all",
            dataSrc: ""
        },
        columns: [
            { data: 'id' },
            { data: 'userName' },
            // { data: 'password' },
            { data: 'roles' },
            {
                render: function ( data, type, row, meta ) {
                    return '<a href="/user/'+row.id+'">Show</a>';
                }

            }
        ]
    });
    console.log("after")
});