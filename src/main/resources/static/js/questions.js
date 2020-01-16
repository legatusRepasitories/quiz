
$(document).ready(function () {
    $('#questionTable').DataTable({
        ajax: {
            url: "/api/question/all",
            dataSrc: ""
        },
        columns: [
            { data: 'id' },
            { data: 'text' },

            {
                render: function ( data, type, row, meta ) {
                    return '<a href="question/'+row.id+'">Edit</a>';
                }
            }
        ]
    });
});