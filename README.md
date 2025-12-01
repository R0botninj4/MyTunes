Here is the structure of my database.

create table PlayList
(
    Name varchar(25),
    ID   int identity
        constraint PlayList_pk
            primary key
)
go

create table Songs
(
    Title           varchar(100) not null,
    Artist          varchar(100) not null,
    category        varchar(100) not null,
    SongID          int identity
        constraint SongID_pk
            primary key nonclustered,
    AudioData       varbinary(max),
    DurationSeconds int
)
go

create table Playlist_Songs
(
    playlist_id int not null
        constraint Playlist_Songs_PlayList_ID_fk
            references PlayList,
    song_id     int not null
        constraint Playlist_Songs_Songs_SongID_fk
            references Songs,
    constraint PK_Playlist_Songs
        primary key (playlist_id, song_id)
)
go

