CREATE TABLE PlayList (
    Name VARCHAR(25),
    ID INT IDENTITY CONSTRAINT PlayList_pk PRIMARY KEY
);
GO

CREATE TABLE Songs (
    Title VARCHAR(100) NOT NULL,
    Artist VARCHAR(100) NOT NULL,
    Category VARCHAR(100) NOT NULL,
    SongID INT IDENTITY CONSTRAINT SongID_pk PRIMARY KEY NONCLUSTERED,
    AudioData VARBINARY(MAX),
    DurationSeconds INT
);
GO

CREATE TABLE Playlist_Songs (
    playlist_id INT NOT NULL CONSTRAINT Playlist_Songs_PlayList_ID_fk REFERENCES PlayList,
    song_id INT NOT NULL CONSTRAINT Playlist_Songs_Songs_SongID_fk REFERENCES Songs,
    CONSTRAINT PK_Playlist_Songs PRIMARY KEY (playlist_id, song_id)
);
GO
