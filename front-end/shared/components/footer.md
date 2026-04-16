## codigo para o footer padrao das telas

## HTML

<footer>
        
        <div id="content-footer">
            <div id="footer-contatos">
                <p>royale concursos</p>
                <h2>Contato</h2>

                <div id="social-links">
                     <a href="https://www.instagram.com/" class="footer-link" id="inta">
                        <i class="fa-brands fa-instagram"></i>
                    </a>

                    <a href="https://github.com/Juao-dev/royale-concursos.git" class="footer-link" id="github">
                        <i class="fa-brands fa-github"></i>
                    </a>

                    <a href="https://www.linkedin.com/feed/?trk=sem-ga_campid.12619604099_asid.149519181115_crid.725790844702_kw.linkedin_d.c_tid.kwd-148086543_n.g_mt.e_geo.9214449" class="footer-link" id="linkedin">
                        <i class="fa-brands fa-linkedin"></i>
                    </a>

                    <a href="#" class="footer-link" id="zapp">
                        <i class="fa-brands fa-whatsapp"></i>
                    </a>

                    <a href="https://youtu.be/8rVIacbAlRg?si=LJ3l8UjpznA469_o" class="footer-link" id="tutube">
                        <i class="fa-brands fa-youtube"></i>
                    </a>
                </div>
            </div>

            <ul class="footer-list">
                <li id="info">
                    informaçao
                </li>

                <li>
                    <a href="#" class="footer-links">Política de privacidade</a>
                </li>

                <li>
                    <a href="#" class="footer-links">Termos de uso</a>
                </li>
                
                <li>
                    <a href="#" class="footer-links">Política de cookies</a>
                </li>

                <li>
                    <a href="#" class="footer-links">Suporte / Ajuda</a>
                </li>
            </ul>

            <div id="footer-sugestoes">
                 <p id="sug">Sugestoes</p>

                 <p>Nos mande por email sugestoes e dicas para adicoes no nosso site.</p>

                 <a href="https://mail.google.com/mail/u/1/#inbox?compose=new" id="email">royaleconcursosJPL@gmail.com</a>
            </div>
        </div>

        <div id="footer-copyright">
            &#169 2026 all rights reserved
        </div>
            
    </footer>

# CSS

footer{
    width: 100%;
    font-family: "DM Sans", sans-serif;
    padding-top: 70px;
}

#content-footer{
    background-color: #000;
    color: white;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    padding: 3rem 3.5rem;
}

#footer-contatos p{
    font-family: "DM Sans", sans-serif;
    font-size: 2rem;
    padding-bottom: 1rem;
}

#social-links{
    padding-top: 1.2rem;
    display: flex;
    gap: 1rem;
}

#social-links .footer-link{
    display: flex;
    align-items: center;
    justify-content: center;
    width: 2.5rem;
    height: 2.5rem;
    border-radius: 50%;
    color: white;
    text-decoration: none;
    
}

#social-links .footer-link:hover{
    transform: translateX(5px) rotate(5deg);
}

#inta{
    background: linear-gradient(#7f37c9, #ff2992, #ff9807);
}

#zapp{
    background: linear-gradient(#01820e, #19ff57, #6fff1c);
}

#linkedin{
    background: linear-gradient(#00065f, #034365, #030033);
}

#github{
    background: linear-gradient(#0b0b0b, #121212, #100f0f);
}

#tutube{
    background: linear-gradient(#ff0000, #690808, #9a4242);
}

#footer-copyright{
    background-color: rgb(2, 2, 2);
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    
}

.footer-list{
    list-style: none;

}

#info{
    font-size: 2rem;
    font-family: "DM Sans", sans-serif;
    padding-bottom: 8px;
}

.footer-links{
    font-family: "DM Sans", sans-serif;
    text-decoration: none;
    font-size: 1.2rem;
    color: white;
}

.footer-links:hover{
    text-decoration: underline;
}

#footer-sugestoes{
    font-size: 1.2rem;

}

#sug{
    font-size: 2rem;
    font-family: "DM Sans", sans-serif;
    padding-bottom: 8px;
    
}

#email{
    text-decoration: underline;
    padding-top: 5px;
    color: white;

}

#email:hover{
   color: #00065f;
}

.nomes-categorias{
    font-size: 2rem;
    border-bottom: 1px solid #333;
}